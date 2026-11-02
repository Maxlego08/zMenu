package fr.maxlego08.menu.hooks.itemsadder.canvas;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.lone.itemsadder.api.Events.ItemsAdderPackCompressedEvent;
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
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * Copies zMenu's canvas files into {@code ItemsAdder/contents/zmenu/resourcepack}, which ItemsAdder merges
 * into its pack on {@code /iazip}. ItemsAdder has no API to add files to its pack.
 * <p>
 * zMenu owns that folder: it lists the files it wrote in {@code contents/zmenu/.zmenu-pack-files}, deletes
 * the ones it no longer generates, and never writes into a {@code contents/zmenu} folder it did not create.
 */
@AutoCanvasPackTarget
@RequiresPlugin("ItemsAdder")
public final class ItemsAdderPackTarget implements CanvasPackTarget, Listener {

    private static final String MANIFEST = ".zmenu-pack-files";

    private final MenuPlugin plugin;
    private BuiltPackState state;
    private volatile String writtenHash;
    private boolean reportedForeignFolder;

    public ItemsAdderPackTarget(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("itemsadder");
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }

    private @Nullable Path folder() {
        Plugin itemsAdder = Bukkit.getPluginManager().getPlugin("ItemsAdder");
        return itemsAdder == null ? null : itemsAdder.getDataFolder().toPath().resolve("contents").resolve("zmenu");
    }

    private @NotNull BuiltPackState state(@NotNull CanvasManager canvasManager) {
        if (this.state == null) this.state = new BuiltPackState(canvasManager.getResourcePackFolder(), "itemsadder");
        return this.state;
    }

    @Override
    public boolean install(@NotNull GeneratedPack pack) {
        CanvasManager canvasManager = this.plugin.getCanvasManager();
        Path folder = this.folder();
        if (canvasManager == null || folder == null) return false;

        Path manifest = folder.resolve(MANIFEST);
        if (Files.isDirectory(folder) && !Files.exists(manifest)) {
            if (!this.reportedForeignFolder) {
                this.reportedForeignFolder = true;
                Logger.info("ItemsAdder/contents/zmenu exists and was not created by zMenu, zMenu does not write its dialog canvas files there. Rename that folder.", Logger.LogType.WARNING);
            }
            return false;
        }

        try {
            this.copy(pack.assets(), folder.resolve("resourcepack"), manifest);
            this.writtenHash = pack.contentHash();
        } catch (IOException exception) {
            Logger.info("Could not copy the dialog canvas files into ItemsAdder/contents/zmenu: " + exception.getMessage(), Logger.LogType.ERROR);
            return false;
        }
        return this.state(canvasManager).isOutdated(pack);
    }

    private void copy(@NotNull Path source, @NotNull Path target, @NotNull Path manifest) throws IOException {
        Set<String> previous = new TreeSet<>(Files.exists(manifest) ? Files.readAllLines(manifest, StandardCharsets.UTF_8) : List.of());
        Set<String> written = new TreeSet<>();

        if (Files.isDirectory(source)) {
            List<Path> files;
            try (Stream<Path> stream = Files.walk(source)) {
                files = stream.filter(Files::isRegularFile).toList();
            }
            for (Path file : files) {
                String relative = source.relativize(file).toString().replace('\\', '/');
                Path destination = target.resolve(relative);
                byte[] bytes = Files.readAllBytes(file);
                if (!Files.exists(destination) || !Arrays.equals(Files.readAllBytes(destination), bytes)) {
                    Files.createDirectories(destination.getParent());
                    Files.write(destination, bytes);
                }
                written.add("resourcepack/" + relative);
            }
        }

        for (String stale : previous) {
            if (written.contains(stale) || stale.contains("..")) continue;
            Files.deleteIfExists(manifest.getParent().resolve(stale));
        }
        Files.createDirectories(manifest.getParent());
        Files.write(manifest, new ArrayList<>(written), StandardCharsets.UTF_8);
    }

    @EventHandler
    public void onLoadData(ItemsAdderLoadDataEvent event) {
        this.plugin.getScheduler().runNextTick(task -> {
            CanvasManager canvasManager = this.plugin.getCanvasManager();
            if (canvasManager != null) canvasManager.regenerate("ItemsAdder loaded its data");
        });
    }

    @EventHandler
    public void onPackCompressed(ItemsAdderPackCompressedEvent event) {
        CanvasManager canvasManager = this.plugin.getCanvasManager();
        if (canvasManager != null && this.writtenHash != null) this.state(canvasManager).built(this.writtenHash);
    }

    @Override
    public void requestRebuild() {
        this.plugin.getScheduler().runNextTick(task -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "iazip"));
    }

    @Override
    public @NotNull String rebuildHint() {
        return "run /iazip";
    }

    @Override
    public @NotNull Optional<byte[]> readPackFile(@NotNull String path) {
        Plugin itemsAdder = Bukkit.getPluginManager().getPlugin("ItemsAdder");
        if (itemsAdder == null) return Optional.empty();
        return new ItemsAdderFontIndex(itemsAdder.getDataFolder().toPath()).readPackFile(path, "zmenu");
    }
}
