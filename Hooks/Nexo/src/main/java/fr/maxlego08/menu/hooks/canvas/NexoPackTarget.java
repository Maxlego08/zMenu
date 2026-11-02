package fr.maxlego08.menu.hooks.canvas;

import com.nexomc.nexo.api.events.resourcepack.NexoPostPackGenerateEvent;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.annotations.AutoCanvasPackTarget;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.dialog.canvas.CanvasManager;
import fr.maxlego08.menu.api.dialog.canvas.CanvasPackTarget;
import fr.maxlego08.menu.api.dialog.canvas.GeneratedPack;
import fr.maxlego08.menu.common.canvas.BuiltPackState;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Adds zMenu's canvas files to the resource pack Nexo builds and hosts.
 * <p>
 * The files are added when Nexo has finished generating its own content, before it validates and
 * obfuscates the pack. Obfuscation never renames font keys and rewrites texture references in every
 * font, so canvases keep working with it.
 */
@AutoCanvasPackTarget
@RequiresPlugin("Nexo")
public final class NexoPackTarget implements CanvasPackTarget, Listener {

    private final MenuPlugin plugin;
    private BuiltPackState state;

    public NexoPackTarget(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("nexo");
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("Nexo");
    }

    private @NotNull BuiltPackState state(@NotNull CanvasManager canvasManager) {
        if (this.state == null) this.state = new BuiltPackState(canvasManager.getResourcePackFolder(), "nexo");
        return this.state;
    }

    @Override
    public boolean install(@NotNull GeneratedPack pack) {
        CanvasManager canvasManager = this.plugin.getCanvasManager();
        return canvasManager != null && this.state(canvasManager).isOutdated(pack);
    }

    @EventHandler
    public void onPackGenerate(NexoPostPackGenerateEvent event) {
        NexoPackAccess.remember(event);
        CanvasManager canvasManager = this.plugin.getCanvasManager();
        if (canvasManager == null || !canvasManager.getActiveTarget().map(target -> target.key().equals(this.key())).orElse(false)) return;

        canvasManager.generateForPackBuild("Nexo is building its resource pack").ifPresent(pack -> {
            if (pack.isEmpty()) {
                this.state(canvasManager).built(pack);
                return;
            }
            if (NexoPackAccess.addFolder(event, pack.assets().toFile())) {
                this.state(canvasManager).built(pack);
            } else {
                Logger.info("Nexo refused the dialog canvas files of " + pack.assets(), Logger.LogType.WARNING);
            }
        });
    }

    @Override
    public void requestRebuild() {
        this.plugin.getScheduler().runNextTick(task -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nexo reload pack"));
    }

    @Override
    public @NotNull String rebuildHint() {
        return "run /nexo reload pack";
    }

    @Override
    public @NotNull Optional<byte[]> readPackFile(@NotNull String path) {
        return NexoPackAccess.readFile(path);
    }
}
