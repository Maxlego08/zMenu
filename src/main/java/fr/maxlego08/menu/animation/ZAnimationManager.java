package fr.maxlego08.menu.animation;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.animation.slot.AnimationHistory;
import fr.maxlego08.menu.api.animation.slot.AnimationManager;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.RegistrationException;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.registry.reload.ReloadContext;
import fr.maxlego08.menu.api.registry.reload.ReloadReport;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.registry.ZRegistryManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Keeps the named animations in the reloadable {@link Registries#ANIMATION} registry: the animations
 * a plugin registers are provided by a provider owned by that plugin, which hands them in again on
 * every reload. Reading the files of the {@code animations} folder is left to an addon providing them.
 */
public class ZAnimationManager implements AnimationManager {

    private static final String OWNER = ZMenuKeys.NAMESPACE;

    private final ZRegistryManager registries;
    private final AnimationHistory history;

    /**
     * What each plugin registered, by plugin name: the source its provider reads on every reload.
     */
    private final Map<String, List<Animation>> registeredByPlugin = new ConcurrentHashMap<>();

    public ZAnimationManager(@NotNull MenuPlugin plugin, @NotNull ZRegistryManager registries) {
        this.registries = registries;
        this.history = new ZAnimationHistory(plugin);
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

        this.logReport(this.registries.update(Registries.ANIMATION, OWNER, editor -> editor.remove(key)));
        return true;
    }

    @Override
    public @NotNull ReloadReport loadAnimations() {
        ReloadReport report = this.registries.reload(Registries.ANIMATION);
        this.logReport(report);

        int count = this.registry().values().size();
        Logger.info("Loaded " + count + " animation" + (count > 1 ? "s" : "") + ".", Logger.LogType.INFO);
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
