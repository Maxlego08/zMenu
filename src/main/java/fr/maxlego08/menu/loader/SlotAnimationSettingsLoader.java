package fr.maxlego08.menu.loader;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.*;
import fr.maxlego08.menu.api.animation.slot.settings.idle.ButtonIdleAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.idle.IdleFrame;
import fr.maxlego08.menu.api.animation.slot.settings.item.AnimationKeyframe;
import fr.maxlego08.menu.api.animation.slot.settings.item.ItemChoice;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.maxlego08.menu.common.utils.cache.YamlFileCacheEntry;
import fr.maxlego08.menu.sound.RandomPitchSoundOption;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads the {@code open-animation} and {@code close-animation} sections of an inventory file, and
 * the {@code animation} section of a button.
 */
public class SlotAnimationSettingsLoader {

    private static final long DEFAULT_INTERVAL = SlotAnimationGroup.DEFAULT_INTERVAL;

    /**
     * The keys that name shared animations, and the weight a variant writes next to them: a section
     * holding nothing else only refers to its files.
     */
    private static final Set<String> REFERENCE_KEYS = Set.of("extends", "file", "files", "weight");

    /**
     * A cooldown, as a number of milliseconds or a number followed by a unit.
     */
    private static final Pattern COOLDOWN_PATTERN = Pattern.compile("^(\\d+)\\s*(ms|s|m|h|d)?$");

    /**
     * Used when an animation says nothing about how it reveals, because it only holds variants or
     * temporary items. Nothing of it is displayed unless something asks for it.
     */
    private static final SlotAnimationGroup FALLBACK_GROUP =
            new SlotAnimationGroup(new SequentialSlotAnimation(), DEFAULT_INTERVAL, 1, 0);

    private final MenuPlugin plugin;
    private final InventoryManager inventoryManager;

    public SlotAnimationSettingsLoader(@NotNull MenuPlugin plugin) {
        this.plugin = plugin;
        this.inventoryManager = plugin.getInventoryManager();
    }

    /**
     * Loads the animation of an inventory.
     *
     * @param configuration the inventory configuration
     * @param path          the path of the animation section, ending with a dot
     * @param file          the inventory file, only used to report errors
     * @return the settings, or null if the inventory has no animation
     */
    @Nullable
    public SlotAnimationSettings load(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        YamlConfiguration shared = this.loadSharedAnimation(configuration, path, file);
        if (shared != null) {
            SlotAnimationSettings settings = this.loadFrom(shared, "", file);
            if (settings == null || this.overridesFiles(configuration, path)) return settings;
            return settings.withFiles(readAnimationNames(configuration, path));
        }

        return this.loadFrom(configuration, path, file);
    }

    /**
     * Whether a section that names shared animations also writes values of its own on top of them.
     */
    private boolean overridesFiles(@NotNull YamlConfiguration configuration, @NotNull String path) {
        return this.sectionValues(configuration, path).keySet().stream().anyMatch(key -> !REFERENCE_KEYS.contains(key));
    }

    /**
     * Reads an animation from a configuration, which is either the inventory file or a shared
     * animation merged with what the inventory overrode.
     */
    @Nullable
    private SlotAnimationSettings loadFrom(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        boolean hasType = configuration.contains(path + "type");
        boolean hasVariants = configuration.isConfigurationSection(path + "variants");
        boolean hasTemporaryItems = configuration.isConfigurationSection(path + "temporary-items")
                || configuration.isConfigurationSection(path + "items");

        if (!hasType && !hasVariants && !hasTemporaryItems) return null;

        SlotAnimationGroup defaults = hasType
                ? this.loadGroup(configuration, path, file, null)
                : this.loadGroup(configuration, path, file, FALLBACK_GROUP);
        if (defaults == null) return null;

        Map<Integer, SlotAnimationGroup> groups = new HashMap<>();
        ConfigurationSection groupSection = configuration.getConfigurationSection(path + "groups");
        if (groupSection != null) {
            for (String key : groupSection.getKeys(false)) {
                int group;
                try {
                    group = Integer.parseInt(key);
                } catch (NumberFormatException exception) {
                    Logger.info("Animation group " + key + " is not a number in " + file.getAbsolutePath(), Logger.LogType.ERROR);
                    continue;
                }
                SlotAnimationGroup loaded = this.loadGroup(configuration, path + "groups." + key + ".", file, defaults);
                if (loaded != null) groups.put(group, loaded);
            }
        }

        boolean freeze = configuration.getBoolean(path + "freeze", true);
        AnimationPlayMode play = AnimationPlayMode.parse(configuration.getString(path + "play"), AnimationPlayMode.ALWAYS);
        List<TemporaryItem> temporaryItems = this.loadTemporaryItems(configuration, path, file);
        Requirement requirement = this.loadRequirement(configuration, path, file);
        float chance = this.loadChance(configuration, path + "chance", "the animation", file);
        List<AnimationVariant> variants = this.loadVariants(configuration, path, file);
        AnimationSeed seed = AnimationSeed.parse(configuration.getString(path + "seed"));

        var buttonManager = this.plugin.getButtonManager();
        List<Action> startActions = buttonManager.loadActions(configuration, path + "start-actions", file);
        List<Action> endActions = buttonManager.loadActions(configuration, path + "end-actions", file);

        long cooldown = this.loadCooldown(configuration, path + "cooldown", file);

        if ((play.isPersistent() || cooldown > 0) && !this.plugin.getStorageManager().isEnable()) {
            Logger.info("The animation in " + file.getAbsolutePath() + " asks to be remembered between sessions, but"
                    + " the database is disabled. It will only be remembered until the player reconnects.", Logger.LogType.WARNING);
        }

        return new SlotAnimationSettings(defaults, groups, freeze, play, temporaryItems, requirement, chance, variants,
                seed, startActions, endActions, cooldown);
    }

    /**
     * Loads the minimum time between two plays.
     *
     * <p>Written either as a plain number of milliseconds, or with a unit so that an hour does not
     * have to be spelled as 3600000.</p>
     *
     * @return the cooldown in milliseconds, 0 for none
     */
    private long loadCooldown(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        if (!configuration.contains(path)) return 0;

        Object value = configuration.get(path);
        if (value instanceof Number number) return Math.max(0, number.longValue());

        String written = String.valueOf(value).toLowerCase(Locale.ROOT).trim();
        Matcher matcher = COOLDOWN_PATTERN.matcher(written);
        if (!matcher.matches()) {
            Logger.info("The animation cooldown " + written + " at " + path + " in " + file.getAbsolutePath()
                    + " is not valid, expected a number of milliseconds or a duration such as 30s, 10m, 2h or 7d.", Logger.LogType.ERROR);
            return 0;
        }

        long amount = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);
        if (unit == null) return amount;

        return switch (unit) {
            case "s" -> amount * 1000L;
            case "m" -> amount * 60_000L;
            case "h" -> amount * 3_600_000L;
            case "d" -> amount * 86_400_000L;
            default -> amount;
        };
    }

    /**
     * Loads the animation of a button. The {@code animation} key accepts either a boolean, to turn
     * the animation off for that button, or a section.
     *
     * @param configuration the inventory configuration
     * @param path          the path of the button, ending with a dot
     * @return the settings, never null
     */
    @NotNull
    public ButtonAnimationSettings loadButton(@NotNull YamlConfiguration configuration, @NotNull String path, boolean allowTemporaryItemsByDefault) {
        Object value = configuration.get(path + "animation");
        if (value == null) {
            return allowTemporaryItemsByDefault ? ButtonAnimationSettings.DEFAULT : ButtonAnimationSettings.NOT_COVERABLE;
        }

        if (value instanceof Boolean enabled) {
            if (!enabled) return ButtonAnimationSettings.DISABLED;
            return allowTemporaryItemsByDefault ? ButtonAnimationSettings.DEFAULT : ButtonAnimationSettings.NOT_COVERABLE;
        }

        boolean enabled = configuration.getBoolean(path + "animation.enabled", true);
        if (!enabled) return ButtonAnimationSettings.DISABLED;

        int group = configuration.getInt(path + "animation.group", 1);
        long delay = configuration.getLong(path + "animation.delay", 0);
        SoundOption sound = this.loadSound(configuration, path + "animation.sound");
        boolean allowTemporaryItems = configuration.getBoolean(path + "animation.allow-temporary-items", allowTemporaryItemsByDefault);

        return new ButtonAnimationSettings(true, group, delay, sound, allowTemporaryItems);
    }

    /**
     * Loads the items that only exist while the animation plays.
     */
    @NotNull
    private List<TemporaryItem> loadTemporaryItems(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        String key = configuration.contains(path + "temporary-items") ? "temporary-items" : "items";

        ConfigurationSection section = configuration.getConfigurationSection(path + key);
        if (section == null) return List.of();

        List<TemporaryItem> temporaryItems = new ArrayList<>();
        Set<Integer> usedSlots = new HashSet<>();

        for (String name : section.getKeys(false)) {
            String itemPath = path + key + "." + name + ".";

            List<Integer> slots = ButtonLoader.loadSlot(configuration.getStringList(itemPath + "slots"));
            if (slots.isEmpty() && configuration.contains(itemPath + "slot")) {
                slots = List.of(configuration.getInt(itemPath + "slot"));
            }
            if (slots.isEmpty()) {
                Logger.info("Animation item " + name + " has no slot in " + file.getAbsolutePath(), Logger.LogType.ERROR);
                continue;
            }

            List<AnimationKeyframe> frames = this.loadKeyframes(configuration, itemPath, name, file);
            if (frames.isEmpty()) {
                Logger.info("Animation item " + name + " has no item and no frames in " + file.getAbsolutePath(), Logger.LogType.ERROR);
                continue;
            }

            for (int slot : slots) {
                if (!usedSlots.add(slot)) {
                    Logger.info("Animation item " + name + " shares the slot " + slot + " with another animation item in " + file.getAbsolutePath() + ", they will overwrite each other", Logger.LogType.WARNING);
                }
            }

            float chance = this.loadChance(configuration, itemPath + "chance", name, file);
            boolean chancePerSlot = configuration.getBoolean(itemPath + "chance-per-slot", configuration.getBoolean(itemPath + "chancePerSlot", false));
            int count = configuration.getInt(itemPath + "count", 0);
            boolean useCache = configuration.getBoolean(itemPath + "use-cache", configuration.getBoolean(itemPath + "useCache", true));
            temporaryItems.add(new TemporaryItem(name, slots, frames, chance, chancePerSlot, count, useCache));
        }

        return temporaryItems;
    }

    /**
     * Loads what the player must satisfy for the animation to play.
     */
    @Nullable
    private Requirement loadRequirement(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        String key = configuration.isConfigurationSection(path + "requirement") ? "requirement"
                : configuration.isConfigurationSection(path + "requirements") ? "requirements" : null;
        if (key == null) return null;

        try {
            return new RequirementLoader(this.plugin).load(configuration, path + key + ".", file);
        } catch (Exception exception) {
            Logger.info("The animation requirement of " + file.getAbsolutePath() + " could not be loaded: " + exception.getMessage(), Logger.LogType.ERROR);
            return null;
        }
    }

    /**
     * Loads the animation a button plays on its own slots for as long as the menu stays open.
     *
     * <p>Like an opening animation, it may be written inline or named with {@code file},
     * {@code files} or {@code extends}, resolved from the animations folder.</p>
     *
     * @param configuration the inventory configuration
     * @param path          the path of the button, ending with a dot
     * @param file          the inventory file, only used to report errors
     * @return the idle animation, or null if the button has none
     */
    @Nullable
    public ButtonIdleAnimation loadIdle(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        String key = configuration.contains(path + "idle-animation") ? "idle-animation"
                : configuration.contains(path + "idle_animation") ? "idle_animation" : null;
        if (key == null) return null;

        String idlePath = path + key + ".";

        YamlConfiguration shared = this.loadSharedAnimation(configuration, idlePath, file);
        if (shared != null) {
            ButtonIdleAnimation idle = this.loadIdleFrom(shared, "", path + key, file);
            if (idle == null || this.overridesFiles(configuration, idlePath)) return idle;
            return idle.withFiles(readAnimationNames(configuration, idlePath));
        }

        return this.loadIdleFrom(configuration, idlePath, path + key, file);
    }

    /**
     * Reads an idle animation from a configuration, which is either the inventory file or a shared
     * animation merged with what the button overrode.
     *
     * @param path     where the values are read from
     * @param reportAt where to say they came from, which stays the path of the button even when the
     *                 values were merged in from a shared file
     */
    @Nullable
    private ButtonIdleAnimation loadIdleFrom(@NotNull YamlConfiguration configuration, @NotNull String path,
                                             @NotNull String reportAt, @NotNull File file) {
        ConfigurationSection section = configuration.getConfigurationSection(path + "items");
        if (section == null) {
            Logger.info("The idle animation at " + reportAt + " needs an items section in " + file.getAbsolutePath(), Logger.LogType.ERROR);
            return null;
        }

        List<IdleFrame> frames = new ArrayList<>();
        for (String name : section.getKeys(false)) {
            String framePath = path + "items." + name + ".";
            long duration = configuration.getLong(framePath + "duration", 0);
            if (duration < 0) {
                Logger.info("The idle frame " + name + " has a negative duration at " + reportAt + ".items." + name
                        + ".duration in " + file.getAbsolutePath() + ", it will use the interval instead.", Logger.LogType.ERROR);
                duration = 0;
            }
            SoundOption frameSound = this.loadSound(configuration, framePath + "sound");
            frames.add(new IdleFrame(this.inventoryManager.loadItemStack(configuration, framePath, file), duration, frameSound));
        }
        if (frames.isEmpty()) {
            Logger.info("The idle animation at " + reportAt + " has no item in " + file.getAbsolutePath(), Logger.LogType.ERROR);
            return null;
        }

        long interval = configuration.getLong(path + "interval", ButtonIdleAnimation.DEFAULT_INTERVAL);
        boolean loop = configuration.getBoolean(path + "loop", true);
        boolean random = configuration.getBoolean(path + "random", false);

        return new ButtonIdleAnimation(frames, interval, loop, random);
    }

    /**
     * Loads what a temporary item does over time.
     *
     * <p>The {@code frames} section is the general form. When it is absent, {@code item} with
     * {@code show} and {@code hide} is loaded as the two keyframes it stands for, so the compiler
     * only ever sees keyframes.</p>
     */
    @NotNull
    private List<AnimationKeyframe> loadKeyframes(@NotNull YamlConfiguration configuration, @NotNull String itemPath, @NotNull String name, @NotNull File file) {
        ConfigurationSection framesSection = configuration.getConfigurationSection(itemPath + "frames");

        if (framesSection == null) {
            List<ItemChoice> choices = this.loadChoices(configuration, itemPath, file);
            boolean hasItem = configuration.contains(itemPath + "item");
            if (!hasItem && choices.isEmpty()) return List.of();

            MenuItemStack menuItemStack = hasItem ? this.inventoryManager.loadItemStack(configuration, itemPath + "item.", file) : null;
            AnimationAnchor show = this.loadAnchor(configuration, itemPath + "show", AnimationAnchor.START, name, file);
            AnimationAnchor hide = this.loadAnchor(configuration, itemPath + "hide", AnimationAnchor.END, name, file);
            SoundOption sound = this.loadSound(configuration, itemPath + "sound");

            return List.of(
                    new AnimationKeyframe(show, menuItemStack, sound, 100, choices),
                    new AnimationKeyframe(hide, null, null));
        }

        List<AnimationKeyframe> frames = new ArrayList<>();
        for (String frameName : framesSection.getKeys(false)) {
            String framePath = itemPath + "frames." + frameName + ".";

            AnimationAnchor at = this.loadAnchor(configuration, framePath + "at", AnimationAnchor.START, name + "." + frameName, file);
            SoundOption sound = this.loadSound(configuration, framePath + "sound");

            boolean removal = configuration.getBoolean(framePath + "remove", false);
            if (removal) {
                if (configuration.contains(framePath + "chance")) {
                    Logger.info("The frame " + name + "." + frameName + " removes the item, so its chance is ignored, in "
                            + file.getAbsolutePath() + ". An item that might not be removed would be left behind.", Logger.LogType.WARNING);
                }
                frames.add(new AnimationKeyframe(at, null, sound));
                continue;
            }

            float frameChance = this.loadChance(configuration, framePath + "chance", name + "." + frameName, file);
            List<ItemChoice> frameChoices = this.loadChoices(configuration, framePath, file);
            if (!frameChoices.isEmpty()) {
                MenuItemStack fallback = configuration.contains(framePath + "item") ? this.inventoryManager.loadItemStack(configuration, framePath + "item.", file) : null;
                frames.add(new AnimationKeyframe(at, fallback, sound, frameChance, frameChoices));
                continue;
            }

            if (!configuration.contains(framePath + "item")) {
                Logger.info("Animation frame " + name + "." + frameName + " has no item, no choices and no remove: true in " + file.getAbsolutePath(), Logger.LogType.ERROR);
                continue;
            }
            frames.add(new AnimationKeyframe(at, this.inventoryManager.loadItemStack(configuration, framePath + "item.", file), sound, frameChance));
        }
        return frames;
    }

    /**
     * Loads the animations named by {@code file}, {@code files} or {@code extends}, merges them in
     * the order they are given, and merges what was written next to them on top.
     *
     * <p>A file may itself name others, so animations can be built from one another: a base motion
     * in one file, an overlay in a second, the menu changing one value on top of both. A file that
     * ends up naming itself, however far down, is reported and skipped.</p>
     *
     * @return the merged configuration, or null if nothing names a shared animation
     */
    @Nullable
    private YamlConfiguration loadSharedAnimation(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        List<String> names = readAnimationNames(configuration, path);
        if (names.isEmpty()) return null;

        YamlConfiguration merged = new YamlConfiguration();
        Set<String> visited = new LinkedHashSet<>();

        for (String name : names) {
            this.mergeAnimationFile(name, merged, visited, file);
        }
        if (visited.isEmpty()) return null;

        merge(this.sectionValues(configuration, path), merged, "");

        merged.set("file", null);
        merged.set("files", null);
        merged.set("extends", null);

        return merged;
    }

    /**
     * Merges one animation file into the target, after whatever that file itself builds on.
     */
    private void mergeAnimationFile(@NotNull String name, @NotNull YamlConfiguration target, @NotNull Set<String> visited, @NotNull File origin) {
        if (!visited.add(name.toLowerCase(Locale.ROOT).trim())) {
            Logger.info("The animation " + name + " is used twice, or builds on itself, in " + origin.getAbsolutePath() + ". It was merged once.", Logger.LogType.WARNING);
            return;
        }

        Path animationPath = this.plugin.getDataFolder().toPath().resolve("animations").resolve(name + ".yml");
        Optional<YamlFileCacheEntry> entry = YamlFileCache.getYamlFileEntry(animationPath);
        if (entry.isEmpty()) {
            Logger.info("Animation file " + name + " does not exist in the animations folder, used by " + origin.getAbsolutePath(), Logger.LogType.ERROR);
            return;
        }

        YamlConfiguration configuration = entry.get().getYamlConfiguration();

        for (String parent : readAnimationNames(configuration, "")) {
            this.mergeAnimationFile(parent, target, visited, origin);
        }

        merge(configuration.getValues(false), target, "");
    }

    /**
     * Reads the animations a section builds on, from {@code files}, {@code file} or {@code extends}.
     */
    @NotNull
    private static List<String> readAnimationNames(@NotNull YamlConfiguration configuration, @NotNull String path) {
        List<String> names = new ArrayList<>();

        for (String key : List.of("extends", "file", "files")) {
            Object value = configuration.get(path + key);
            if (value == null) continue;

            if (value instanceof List<?> list) {
                list.forEach(name -> names.add(String.valueOf(name)));
            } else {
                names.add(String.valueOf(value));
            }
        }
        names.removeIf(name -> name == null || name.isBlank());
        return names;
    }

    /**
     * Copies values into a configuration, going into the sections rather than replacing them, so an
     * inventory can override a single key of a group without redefining the whole group.
     */
    private static void merge(@NotNull Map<String, Object> values, @NotNull YamlConfiguration target, @NotNull String prefix) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = prefix + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof ConfigurationSection section) {
                merge(section.getValues(false), target, key + ".");
            } else if (value instanceof Map<?, ?> map) {
                Map<String, Object> casted = new LinkedHashMap<>();
                map.forEach((mapKey, mapValue) -> casted.put(String.valueOf(mapKey), mapValue));
                merge(casted, target, key + ".");
            } else {
                target.set(key, value);
            }
        }
    }

    /**
     * Gets the values of the section a path points at, so that an animation may read its own options.
     */
    @NotNull
    private Map<String, Object> sectionValues(@NotNull YamlConfiguration configuration, @NotNull String path) {
        String sectionPath = path.endsWith(".") ? path.substring(0, path.length() - 1) : path;
        if (sectionPath.isEmpty()) return configuration.getValues(false);

        ConfigurationSection section = configuration.getConfigurationSection(sectionPath);
        return section == null ? Map.of() : section.getValues(false);
    }

    /**
     * Loads the alternatives one of which is picked when the animation plays.
     */
    @NotNull
    private List<AnimationVariant> loadVariants(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        ConfigurationSection section = configuration.getConfigurationSection(path + "variants");
        if (section == null) return List.of();

        List<AnimationVariant> variants = new ArrayList<>();
        for (String name : section.getKeys(false)) {
            String variantPath = path + "variants." + name + ".";

            SlotAnimationSettings settings = this.load(configuration, variantPath, file);
            if (settings == null) {
                Logger.info("The animation variant " + name + " has no type in " + file.getAbsolutePath(), Logger.LogType.ERROR);
                continue;
            }
            if (!settings.variants().isEmpty()) {
                Logger.info("The animation variant " + name + " has variants of its own in " + file.getAbsolutePath() + ", they are ignored.", Logger.LogType.WARNING);
            }

            variants.add(new AnimationVariant(name, settings, configuration.getInt(variantPath + "weight", 1)));
        }
        return variants;
    }

    /**
     * Loads the items a keyframe may pick from, each with an optional weight.
     */
    @NotNull
    private List<ItemChoice> loadChoices(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file) {
        ConfigurationSection section = configuration.getConfigurationSection(path + "choices");
        if (section == null) return List.of();

        List<ItemChoice> choices = new ArrayList<>();
        for (String name : section.getKeys(false)) {
            String choicePath = path + "choices." + name + ".";
            int weight = configuration.getInt(choicePath + "weight", 1);
            choices.add(new ItemChoice(this.inventoryManager.loadItemStack(configuration, choicePath, file), weight));
        }
        return choices;
    }

    /**
     * Reads a chance in percent, where 100 always happens and 0 never does.
     */
    private float loadChance(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull String name, @NotNull File file) {
        if (!configuration.contains(path)) return 100;

        double chance = configuration.getDouble(path, 100);
        if (chance < 0 || chance > 100) {
            Logger.info("The chance of " + name + " is " + chance + " in " + file.getAbsolutePath() + ", it must be between 0 and 100.", Logger.LogType.ERROR);
            return 100;
        }
        return (float) chance;
    }

    @Nullable
    private SoundOption loadSound(@NotNull YamlConfiguration configuration, @NotNull String path) {
        String sound = configuration.getString(path + ".sound");
        if (sound == null || sound.isEmpty()) return null;

        float volume = (float) configuration.getDouble(path + ".volume", 1);
        String category = configuration.getString(path + ".sound-category", configuration.getString(path + ".category", XSound.Category.MASTER.name()));
        XSound xSound = XSound.of(sound).orElse(null);

        List<Double> pitches = configuration.getDoubleList(path + ".pitch");
        if (pitches.size() >= 2) {
            return new RandomPitchSoundOption(xSound, category, sound, pitches.get(0).floatValue(), pitches.get(1).floatValue(), volume, xSound == null);
        }

        float pitch = (float) configuration.getDouble(path + ".pitch", 1);
        return new ZSoundOption(xSound, category, sound, pitch, volume, xSound == null);
    }

    @NotNull
    private AnimationAnchor loadAnchor(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull AnimationAnchor defaultValue, @NotNull String name, @NotNull File file) {
        if (!configuration.contains(path)) return defaultValue;

        ConfigurationSection section = configuration.getConfigurationSection(path);
        Object value = section == null ? configuration.get(path) : section.getValues(false);

        AnimationAnchor anchor = AnimationAnchor.parseValue(value);
        if (anchor == null) {
            Logger.info("Animation item " + name + " has an invalid time at " + path + " in " + file.getAbsolutePath() + ", expected a number of milliseconds, start, end, group:N, group:N.end, or a section with a group and at: start/end", Logger.LogType.ERROR);
            return defaultValue;
        }
        return anchor;
    }

    /**
     * Reads a {@code type}: without a namespace it belongs to zmenu, so {@code spiral} is {@code zmenu:spiral}.
     */
    private Optional<SlotAnimationLoader> findAnimation(@NotNull KeyedRegistry<SlotAnimationLoader> registry, @NotNull String type) {
        try {
            return registry.get(ZMenuKeys.parse(type));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    @Nullable
    private SlotAnimationGroup loadGroup(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file, @Nullable SlotAnimationGroup defaults) {
        SlotAnimation animation;

        if (configuration.contains(path + "type")) {
            String type = configuration.getString(path + "type");
            KeyedRegistry<SlotAnimationLoader> registry = this.plugin.getInventoryManager().getRegistries().registry(Registries.SLOT_ANIMATION);
            Optional<SlotAnimationLoader> optional = this.findAnimation(registry, type);
            if (optional.isEmpty()) {
                Logger.info("Animation type " + type + " is not valid for inventory " + file.getAbsolutePath() + ", available types: " + registry.keys(), Logger.LogType.ERROR);
                return defaults;
            }

            try {
                animation = optional.get().create(this.sectionValues(configuration, path));
            } catch (IllegalArgumentException exception) {
                Logger.info("Animation type " + type + " cannot be used in " + file.getAbsolutePath() + ": " + exception.getMessage(), Logger.LogType.ERROR);
                return defaults;
            }
        } else if (defaults != null) {
            animation = defaults.animation();
        } else {
            return null;
        }

        long interval = configuration.getLong(path + "interval", defaults == null ? DEFAULT_INTERVAL : defaults.interval());
        int slotsPerStep = configuration.getInt(path + "slots-per-step", configuration.getInt(path + "slotsPerStep", defaults == null ? 1 : defaults.slotsPerStep()));
        long delay = configuration.getLong(path + "delay", 0);

        SoundOption sound = this.loadSound(configuration, path + "sound");
        if (sound == null && defaults != null) sound = defaults.sound();

        boolean reverse = configuration.getBoolean(path + "reverse", defaults != null && defaults.reverse());
        long jitter = configuration.getLong(path + "interval-jitter", defaults == null ? 0 : defaults.jitter());

        return new SlotAnimationGroup(animation, interval, slotsPerStep, delay, sound, reverse, jitter);
    }
}
