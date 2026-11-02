package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasValue;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Draws a sprite of one of the client's texture atlases, 8 pixels wide and high, on a text line
 * (Minecraft 1.21.9+). Unlike a {@code minecraft:} glyph, it needs nothing in the resource pack, and it
 * also shows textures of other resource packs the player loaded.
 *
 * <pre>
 * icon:
 *   type: sprite
 *   atlas: minecraft:blocks   # optional, the atlas holding the sprite
 *   sprite: block/stone       # may be a placeholder, such as "item/%my_plugin_item%"
 *   x: 4
 *   line: 2                   # drawn at canvas y 9 × line - 1
 * </pre>
 *
 * @param name   The name of the element.
 * @param atlas  The atlas.
 * @param sprite The sprite inside the atlas.
 * @param x      The x of its left edge.
 * @param line   The text line it is drawn on.
 */
public record SpriteElement(@NotNull String name, @NotNull CanvasValue<Key> atlas, @NotNull CanvasValue<Key> sprite, @NotNull PositionValue x,
                            int line) implements CanvasElement {

    public static final Key DEFAULT_ATLAS = Key.key(Key.MINECRAFT_NAMESPACE, "blocks");
    public static final Key MISSING_SPRITE = Key.key(Key.MINECRAFT_NAMESPACE, "missingno");

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        if (!ObjectElements.supported(context)) return;
        Component sprite = Component.object(ObjectContents.sprite(this.atlas.resolve(context), this.sprite.resolve(context)));
        builder.inline(sprite, ObjectElements.SIZE, this.x.resolve(context), this.line);
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", "sprite");
        if (this.atlas.isPlaceholder() || !DEFAULT_ATLAS.equals(this.atlas.fixed())) this.atlas.serialize(section, "atlas");
        this.sprite.serialize(section, "sprite");
        this.x.serialize(section, "x");
        section.set("line", this.line);
    }
}
