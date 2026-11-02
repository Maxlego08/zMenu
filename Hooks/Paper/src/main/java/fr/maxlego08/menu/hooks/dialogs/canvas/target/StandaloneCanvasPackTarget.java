package fr.maxlego08.menu.hooks.dialogs.canvas.target;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.CanvasManager;
import fr.maxlego08.menu.api.dialog.canvas.CanvasPackTarget;
import fr.maxlego08.menu.api.dialog.canvas.GeneratedPack;
import fr.maxlego08.menu.common.canvas.BuiltPackState;
import fr.maxlego08.menu.hooks.dialogs.canvas.CanvasSettings;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * Sends zMenu's own pack to players, from a URL where the server owner uploads {@code zmenu-pack.zip}. zMenu
 * does not host the file. The pack is sent with its SHA-1, so a client refuses a file that was not
 * uploaded again after the canvases changed: zMenu says so in the console each time the zip changes.
 * <p>
 * It is sent next to the other packs of the server, and again to online players when it changes.
 */
public final class StandaloneCanvasPackTarget implements CanvasPackTarget, Listener {

    public static final String NAME = "standalone";
    public static final UUID PACK_ID = UUID.nameUUIDFromBytes("zmenu:dialog-canvas".getBytes(StandardCharsets.UTF_8));

    private final MenuPlugin plugin;
    private final CanvasManager canvasManager;
    private BuiltPackState state;

    public StandaloneCanvasPackTarget(@NotNull MenuPlugin plugin, @NotNull CanvasManager canvasManager) {
        this.plugin = plugin;
        this.canvasManager = canvasManager;
    }

    private @NotNull CanvasSettings settings() {
        return CanvasSettings.read(this.plugin.getConfig());
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key(NAME);
    }

    @Override
    public boolean isAvailable() {
        return !this.settings().standaloneUrl().isBlank();
    }

    @Override
    public boolean install(@NotNull GeneratedPack pack) {
        if (this.state == null) this.state = new BuiltPackState(this.canvasManager.getResourcePackFolder(), NAME);
        if (pack.isEmpty() || !this.state.isOutdated(pack)) return false;
        this.state.built(pack);
        Logger.info("The dialog canvas pack changed: upload " + pack.zip() + " to " + this.settings().standaloneUrl() + " (SHA-1 " + pack.sha1() + ")");
        this.plugin.getScheduler().runNextTick(task -> Bukkit.getOnlinePlayers().forEach(this::send));
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (this.isActive()) this.send(event.getPlayer());
    }

    private boolean isActive() {
        return this.canvasManager.getActiveTarget().map(target -> target.key().equals(this.key())).orElse(false);
    }

    private void send(@NotNull Player player) {
        Optional<GeneratedPack> pack = this.canvasManager.getGeneratedPack();
        if (pack.isEmpty() || pack.get().isEmpty()) return;
        ResourcePackRequest request = this.request(pack.get());
        if (request != null) player.sendResourcePacks(request);
    }

    private @Nullable ResourcePackRequest request(@NotNull GeneratedPack pack) {
        CanvasSettings settings = this.settings();
        URI uri;
        try {
            uri = URI.create(settings.standaloneUrl());
        } catch (IllegalArgumentException exception) {
            Logger.info("The dialog canvas pack URL " + settings.standaloneUrl() + " is not valid", Logger.LogType.WARNING);
            return null;
        }
        ResourcePackRequest.Builder builder = ResourcePackRequest.resourcePackRequest()
                .packs(ResourcePackInfo.resourcePackInfo(PACK_ID, uri, pack.sha1()))
                .required(settings.standaloneRequired())
                .replace(false);
        if (!settings.standalonePrompt().isBlank()) builder.prompt(MiniMessage.miniMessage().deserialize(settings.standalonePrompt()));
        return builder.build();
    }

    @Override
    public void requestRebuild() {
        this.plugin.getScheduler().runNextTick(task -> Bukkit.getOnlinePlayers().forEach(this::send));
    }

    @Override
    public @NotNull String rebuildHint() {
        return "upload the new zmenu-pack.zip to " + this.settings().standaloneUrl();
    }
}
