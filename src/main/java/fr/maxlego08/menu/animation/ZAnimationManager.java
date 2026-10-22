package fr.maxlego08.menu.animation;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.animation.slot.AnimationHistory;
import fr.maxlego08.menu.api.animation.slot.AnimationManager;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.RegistrationException;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.registry.reload.ReloadContext;
import fr.maxlego08.menu.api.registry.reload.ReloadReport;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.loader.SlotAnimationSettingsLoader;
import fr.maxlego08.menu.registry.ZRegistryManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * Keeps the named animations in the reloadable {@link Registries#ANIMATION} registry: the files of
 * the {@code animations} folder are provided by {@code zmenu}, and the animations a plugin registers
 * by a provider owned by that plugin, which hands them in again on every reload.
 */
public class ZAnimationManager implements AnimationManager {

    private static final String FOLDER = "animations";
    private static final String FILES_OWNER = ZMenuKeys.NAMESPACE;

    private final MenuPlugin plugin;
    private final ZRegistryManager registries;
    private final AnimationHistory history;

    /**
     * What each plugin registered, by plugin name: the source its provider reads on every reload.
     */
    private final Map<String, List<Animation>> registeredByPlugin = new ConcurrentHashMap<>();

    public ZAnimationManager(@NotNull MenuPlugin plugin, @NotNull ZRegistryManager registries) {
        this.plugin = plugin;
        this.registries = registries;
        this.history = new ZAnimationHistory(plugin);
        this.registries.provider(Registries.ANIMATION, FILES_OWNER, this::provideFiles);
    }

    @Override
    public @NotNull AnimationHistory getHistory() {
        return this.history;
    }

    private KeyedRegistry<Animation> registry() {
        return this.registries.registry(Registries.ANIMATION);
    }

    @Override
    public @NotNull Optional<Animation> getAnimation(@NotNull Key key) {
        return this.registry().get(key);
    }

    @Override
    public @NotNull Optional<Animation> findAnimation(@NotNull String name) {
        return this.getAnimation(ZMenuKeys.inventoryKey(name));
    }

    @Override
    public @NotNull Collection<Animation> getAnimations() {
        return this.registry().values();
    }

    @Override
    public void registerAnimation(@NotNull Plugin plugin, @NotNull Animation animation) {
        if (this.registry().contains(animation.key())) {
            String owner = this.registry().owner(animation.key()).orElse("?");
            throw new RegistrationException("The animation " + animation.name() + " is already registered by " + owner + ", " + plugin.getName() + " cannot register it again");
        }

        String owner = plugin.getName();
        List<Animation> animations = this.registeredByPlugin.computeIfAbsent(owner, name -> {
            this.registries.provider(Registries.ANIMATION, name, context -> this.provideRegistered(name, context));
            return new CopyOnWriteArrayList<>();
        });
        animations.add(animation);

        this.logReport(this.registries.update(Registries.ANIMATION, owner, editor -> editor.register(animation)));
    }

    @Override
    public boolean unregisterAnimation(@NotNull Key key) {
        boolean removed = false;
        for (List<Animation> animations : this.registeredByPlugin.values()) {
            removed |= animations.removeIf(animation -> animation.key().equals(key));
        }
        if (!removed) return false;

        this.logReport(this.registries.update(Registries.ANIMATION, FILES_OWNER, editor -> editor.remove(key)));
        return true;
    }

    @Override
    public @NotNull ReloadReport loadAnimations() {
        ReloadReport report = this.registries.reload(Registries.ANIMATION);
        this.logReport(report);

        long files = this.registry().values().stream()
                .filter(animation -> this.registry().owner(animation.key()).map(FILES_OWNER::equals).orElse(false))
                .count();
        Logger.info("Loaded " + files + " animation" + (files > 1 ? "s" : "") + ".", Logger.LogType.INFO);
        return report;
    }

    private void provideRegistered(@NotNull String owner, @NotNull ReloadContext<Animation> context) {
        KeyedRegistry<Animation> next = context.registries().registry(Registries.ANIMATION);
        for (Animation animation : this.registeredByPlugin.getOrDefault(owner, List.of())) {
            if (next.contains(animation.key())) {
                context.warn(animation.key(), "The animation " + animation.name() + " of " + owner + " has the key of another animation, it is not loaded");
                continue;
            }
            context.register(animation);
        }
    }

    /**
     * Provides the files of the animations folder. A file that cannot be read keeps its previous
     * version, and two files with the same name in different folders keep the first one.
     */
    private void provideFiles(@NotNull ReloadContext<Animation> context) {
        File folder = new File(this.plugin.getDataFolder(), FOLDER);
        if (!folder.exists() && !folder.mkdirs()) {
            context.warn("Could not create the " + FOLDER + " folder");
            return;
        }

        KeyedRegistry<Animation> next = context.registries().registry(Registries.ANIMATION);
        SlotAnimationSettingsLoader loader = new SlotAnimationSettingsLoader(this.plugin);

        for (File file : this.animationFiles(folder, context)) {
            String name = file.getName().substring(0, file.getName().length() - ".yml".length());
            Key key = ZMenuKeys.fileKey(FILES_OWNER, name);

            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            if (isIdleAnimation(configuration)) continue;

            if (next.contains(key)) {
                context.warn(key, "Two animation files are named " + name + ", the one in " + file.getAbsolutePath()
                        + " is not loaded. Animations are known by their file name, whichever folder they sit in.");
                continue;
            }

            SlotAnimationSettings settings;
            try {
                settings = loader.load(configuration, "", file);
            } catch (RuntimeException exception) {
                Logger.error(exception);
                settings = null;
            }

            if (settings != null) {
                context.register(new Animation(key, settings));
                continue;
            }

            Optional<Animation> previous = context.previous().get(key);
            if (previous.isPresent()) {
                context.register(previous.get());
                context.warn(key, file.getAbsolutePath() + " cannot be loaded, its previous version is kept");
            } else {
                context.warn(key, "Animation " + file.getAbsolutePath() + " has no type, it was not loaded");
            }
        }
    }

    @NotNull
    private List<File> animationFiles(@NotNull File folder, @NotNull ReloadContext<Animation> context) {
        List<File> files = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(folder.toPath())) {
            stream.map(Path::toFile).filter(File::isFile).filter(file -> file.getName().endsWith(".yml")).forEach(files::add);
        } catch (IOException exception) {
            context.warn("Could not read the " + FOLDER + " folder: " + exception.getMessage());
        }
        return files;
    }

    /**
     * Whether a file of the animations folder is the idle animation of a button rather than an
     * animation of the whole inventory. Its {@code items} are frames, played on the slots of the
     * button that names it, so none of them has a slot of its own; it is read only when a button
     * names it with {@code idle-animation}.
     */
    private static boolean isIdleAnimation(@NotNull YamlConfiguration configuration) {
        if (configuration.contains("type") || configuration.contains("variants") || configuration.contains("temporary-items")) return false;

        ConfigurationSection items = configuration.getConfigurationSection("items");
        if (items == null || items.getKeys(false).isEmpty()) return false;

        return items.getKeys(false).stream()
                .map(items::getConfigurationSection)
                .allMatch(item -> item != null && !item.contains("slot") && !item.contains("slots"));
    }

    private void logReport(@NotNull ReloadReport report) {
        for (ReloadReport.Issue issue : report.issues()) {
            String line = (issue.subject() == null ? "" : issue.subject().asString() + ": ") + issue.message();
            Logger.info(line, issue.severity() == ReloadReport.Severity.ERROR ? Logger.LogType.ERROR : Logger.LogType.WARNING);
        }
        if (!report.applied()) {
            Logger.info("The reload of the animations was rejected, the previous version stays loaded.", Logger.LogType.ERROR);
        }
    }

    @Override
    public long play(@NotNull Player player, @NotNull Key key, @NotNull AnimationPlayback playback) {
        return this.getAnimation(key).map(animation -> this.play(player, animation, playback)).orElse(0L);
    }

    @Override
    public long play(@NotNull Player player, @NotNull Animation animation, @NotNull AnimationPlayback playback) {
        InventoryEngine engine = this.getOpenEngine(player);
        return engine == null ? 0 : engine.playAnimation(animation, playback);
    }

    @Override
    public void stop(@NotNull Player player) {
        InventoryEngine engine = this.getOpenEngine(player);
        if (engine != null) engine.stopAnimation();
    }

    /**
     * Gets the zMenu inventory the player is looking at, if any.
     */
    @Nullable
    private InventoryEngine getOpenEngine(@NotNull Player player) {
        InventoryHolder holder = CompatibilityUtil.getTopInventory(player).getHolder();
        return holder instanceof InventoryEngine engine ? engine : null;
    }
}
