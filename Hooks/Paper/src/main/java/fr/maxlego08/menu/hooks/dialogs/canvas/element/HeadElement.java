package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasValue;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Draws the head of a player, 8 pixels wide and high, on a text line (Minecraft 1.21.9+).
 *
 * <pre>
 * head:
 *   type: head
 *   player: "%player_name%"   # a name, a UUID or a placeholder; the viewer by default
 *   texture: "eyJ0ZXh0..."    # optional, a skin texture value instead of a player; a placeholder giving
 *                             # an empty result falls back to the player
 *   hat: true                 # optional, draws the hat layer; may be a placeholder
 *   x: 4
 *   line: 1                   # drawn at canvas y 9 × line - 1
 * </pre>
 *
 * @param name    The name of the element.
 * @param player  The player whose head is drawn, or null when {@code texture} is set.
 * @param texture The value of a skin texture, or null.
 * @param hat     True to draw the hat layer.
 * @param x       The x of its left edge.
 * @param line    The text line it is drawn on.
 */
public record HeadElement(@NotNull String name, @Nullable String player, @Nullable CanvasValue<String> texture, @NotNull CanvasValue<Boolean> hat,
                          @NotNull PositionValue x, int line) implements CanvasElement {

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        if (!ObjectElements.supported(context)) return;
        PlayerHeadObjectContents.Builder head = ObjectContents.playerHead().hat(this.hat.resolve(context));
        String skin = this.texture == null ? null : this.texture.resolve(context);
        if (skin != null && !skin.isBlank()) {
            head.profileProperty(PlayerHeadObjectContents.property("textures", skin));
        } else {
            String value = context.resolve(this.player == null ? "%player_name%" : this.player).trim();
            UUID uuid = parseUuid(value);
            if (uuid != null) {
                head.id(uuid);
            } else {
                head.name(value);
            }
        }
        builder.inline(Component.object(head.build()), ObjectElements.SIZE, this.x.resolve(context), this.line);
    }

    private static @Nullable UUID parseUuid(@NotNull String value) {
        if (value.length() != 36) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", "head");
        if (this.player != null) section.set("player", this.player);
        if (this.texture != null) this.texture.serialize(section, "texture");
        if (this.hat.isPlaceholder() || Boolean.FALSE.equals(this.hat.fixed())) this.hat.serialize(section, "hat");
        this.x.serialize(section, "x");
        section.set("line", this.line);
    }
}
