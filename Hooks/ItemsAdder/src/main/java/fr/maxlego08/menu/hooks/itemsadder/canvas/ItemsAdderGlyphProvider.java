package fr.maxlego08.menu.hooks.itemsadder.canvas;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.annotations.AutoGlyphProvider;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.GlyphProvider;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The font images of ItemsAdder, referenced as {@code itemsadder:<namespace>:<id>}.
 * <p>
 * ItemsAdder draws every font image in the {@code minecraft:default} font, which is where a canvas draws it
 * when the element gives no other position.
 */
@AutoGlyphProvider
@RequiresPlugin("ItemsAdder")
public final class ItemsAdderGlyphProvider implements GlyphProvider {

    private static final Key DEFAULT_FONT = Key.key(Key.MINECRAFT_NAMESPACE, "default");

    private final ItemsAdderFontIndex index;
    private final Map<Key, Optional<BufferedImage>> textures = new ConcurrentHashMap<>();
    private final Map<Key, ItemsAdderFontIndex.Entry> owners = new ConcurrentHashMap<>();

    public ItemsAdderGlyphProvider() {
        Plugin itemsAdder = Bukkit.getPluginManager().getPlugin("ItemsAdder");
        this.index = new ItemsAdderFontIndex(itemsAdder == null ? Bukkit.getWorldContainer().toPath().resolve("plugins/ItemsAdder") : itemsAdder.getDataFolder().toPath());
        this.index.reload();
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("itemsadder");
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }

    @Override
    public @NotNull Optional<ResolvedGlyph> resolve(@NotNull String path) {
        String character = FontImageWrapper.getNamespacedIdsAndValueInRegistry().get(path);
        if (character == null || character.isEmpty()) return Optional.empty();

        ItemsAdderFontIndex.Entry entry = this.index.get(path);
        if (entry == null) {
            Logger.info("The ItemsAdder font image " + path + " is not defined in a file of ItemsAdder/contents, a canvas cannot read its texture", Logger.LogType.WARNING);
            return Optional.empty();
        }
        this.owners.put(entry.texture(), entry);

        Optional<BufferedImage> image = this.readTexture(entry.texture());
        if (image.isEmpty()) {
            Logger.info("The texture " + entry.texture().asString() + " of the ItemsAdder font image " + path + " cannot be read, a canvas cannot measure it", Logger.LogType.WARNING);
            return Optional.empty();
        }
        BufferedImage value = image.get();
        int ink = TextureMetrics.inkWidth(value, 0, 0, value.getWidth(), value.getHeight());
        int ascent = Math.min(entry.ascent(), entry.height());

        GlyphCell cell = new GlyphCell(entry.texture(), 1, 1, 0, 0, value.getWidth(), value.getHeight(), entry.height(), ink, DEFAULT_FONT, character, ascent);
        return Optional.of(new ResolvedGlyph("itemsadder:" + path, List.of(cell), false));
    }

    @Override
    public @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture) {
        return this.textures.computeIfAbsent(texture, key -> {
            ItemsAdderFontIndex.Entry entry = this.owners.get(key);
            return this.index.readTexture(key, entry == null ? null : entry.folder());
        });
    }

    @Override
    public void reload() {
        this.textures.clear();
        this.owners.clear();
        this.index.reload();
    }
}
