package fr.maxlego08.menu.hooks.canvas;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.annotations.AutoCanvasPackTarget;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.dialog.canvas.CanvasManager;
import fr.maxlego08.menu.api.dialog.canvas.CanvasPackTarget;
import fr.maxlego08.menu.api.dialog.canvas.GeneratedPack;
import fr.maxlego08.menu.common.canvas.BuiltPackState;
import net.kyori.adventure.key.Key;
import net.momirealms.craftengine.bukkit.api.event.AsyncResourcePackCacheEvent;
import net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent;
import net.momirealms.craftengine.core.pack.Pack;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Adds zMenu's canvas files to the resource pack CraftEngine builds and hosts.
 * <p>
 * CraftEngine asks for external files at the start of every build, from another thread: zMenu generates
 * its files then, so they use the images CraftEngine just loaded, and registers its output folder. CraftEngine
 * only rereads a file whose size or date changed, and zMenu only rewrites the files whose content changed.
 */
@AutoCanvasPackTarget
@RequiresPlugin("CraftEngine")
public final class CraftEnginePackTarget implements CanvasPackTarget, Listener {

    private final MenuPlugin plugin;
    private BuiltPackState state;

    public CraftEnginePackTarget(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("craftengine");
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("CraftEngine");
    }

    private synchronized @NotNull BuiltPackState state(@NotNull CanvasManager canvasManager) {
        if (this.state == null) this.state = new BuiltPackState(canvasManager.getResourcePackFolder(), "craftengine");
        return this.state;
    }

    @Override
    public boolean install(@NotNull GeneratedPack pack) {
        CanvasManager canvasManager = this.plugin.getCanvasManager();
        return canvasManager != null && this.state(canvasManager).isOutdated(pack);
    }

    @EventHandler
    public void onPackCache(AsyncResourcePackCacheEvent event) {
        CanvasManager canvasManager = this.plugin.getCanvasManager();
        if (canvasManager == null || !canvasManager.getActiveTarget().map(target -> target.key().equals(this.key())).orElse(false)) return;

        canvasManager.generateForPackBuild("CraftEngine is building its resource pack").ifPresent(pack -> {
            if (!pack.isEmpty()) event.registerExternalResourcePack(pack.assets());
            this.state(canvasManager).built(pack);
        });
    }

    @EventHandler
    public void onReload(CraftEngineReloadEvent event) {
        this.plugin.getScheduler().runNextTick(task -> {
            CanvasManager canvasManager = this.plugin.getCanvasManager();
            if (canvasManager != null) canvasManager.regenerate("CraftEngine loaded its images");
        });
    }

    @Override
    public void requestRebuild() {
        this.plugin.getScheduler().runNextTick(task -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ce reload pack"));
    }

    @Override
    public @NotNull String rebuildHint() {
        return "run /ce reload pack";
    }

    @Override
    public @NotNull Optional<byte[]> readPackFile(@NotNull String path) {
        if (path.contains("..")) return Optional.empty();
        for (Pack pack : CraftEngine.instance().packManager().loadedPacks()) {
            if (!pack.enabled()) continue;
            for (Path folder : pack.resourcePackFolders()) {
                Path file = folder.resolve(path);
                if (!Files.isRegularFile(file)) continue;
                try {
                    return Optional.of(Files.readAllBytes(file));
                } catch (IOException ignored) {
                }
            }
        }
        return Optional.empty();
    }
}
