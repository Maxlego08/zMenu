package fr.maxlego08.menu.animation;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.animation.slot.AnimationManager;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.loader.SlotAnimationSettingsLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ZAnimationManager implements AnimationManager {

    private static final String FOLDER = "animations";

    private final MenuPlugin plugin;
    private final Map<String, Animation> animations = new ConcurrentHashMap<>();

    /**
     * Names registered by a plugin rather than loaded from a file, so a reload of the folder does
     * not drop them.
     */
    private final Set<String> registered = ConcurrentHashMap.newKeySet();

    public ZAnimationManager(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Optional<Animation> getAnimation(@NotNull String name) {
        return Optional.ofNullable(this.animations.get(normalize(name)));
    }

    @Override
    public @NotNull Collection<Animation> getAnimations() {
        return Collections.unmodifiableCollection(this.animations.values());
    }

    @Override
    public void registerAnimation(@NotNull Animation animation) {
        String key = normalize(animation.name());
        this.animations.put(key, animation);
        this.registered.add(key);
    }

    @Override
    public void unregisterAnimation(@NotNull String name) {
        String key = normalize(name);
        this.animations.remove(key);
        this.registered.remove(key);
    }

    @Override
    public void loadAnimations() {
        this.animations.keySet().removeIf(key -> !this.registered.contains(key));

        File folder = new File(this.plugin.getDataFolder(), FOLDER);
        if (!folder.exists() && !folder.mkdirs()) {
            Logger.info("Could not create the " + FOLDER + " folder!", Logger.LogType.ERROR);
            return;
        }

        SlotAnimationSettingsLoader loader = new SlotAnimationSettingsLoader(this.plugin);
        Set<String> loaded = new HashSet<>();

        try (Stream<Path> stream = Files.walk(Paths.get(folder.getPath()))) {
            stream.map(Path::toFile)
                    .filter(File::isFile)
                    .filter(file -> file.getName().endsWith(".yml"))
                    .forEach(file -> {
                        String name = file.getName().substring(0, file.getName().length() - ".yml".length());
                        String key = normalize(name);

                        if (this.registered.contains(key)) {
                            Logger.info("Animation " + name + " is registered by a plugin, the file is ignored.", Logger.LogType.WARNING);
                            return;
                        }

                        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                        SlotAnimationSettings settings = loader.load(configuration, "", file);
                        if (settings == null) {
                            Logger.info("Animation " + file.getAbsolutePath() + " has no type, it was not loaded.", Logger.LogType.ERROR);
                            return;
                        }

                        if (!loaded.add(name)) {
                            Logger.info("Two animation files are named " + name + ", the one in " + file.getAbsolutePath()
                                    + " replaced the other. Animations are known by their file name, whichever folder they sit in.",
                                    Logger.LogType.WARNING);
                        }
                        this.animations.put(key, new Animation(name, settings));
                    });
        } catch (Exception exception) {
            Logger.info("Could not read the " + FOLDER + " folder: " + exception.getMessage(), Logger.LogType.ERROR);
            return;
        }

        Logger.info("Loaded " + loaded.size() + " animation" + (loaded.size() > 1 ? "s" : "") + ".", Logger.LogType.INFO);
    }

    @Override
    public long play(@NotNull Player player, @NotNull String name, @NotNull AnimationPlayback playback) {
        return this.getAnimation(name).map(animation -> this.play(player, animation, playback)).orElse(0L);
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

    private static String normalize(@NotNull String name) {
        return name.toLowerCase(Locale.ROOT).trim();
    }
}
