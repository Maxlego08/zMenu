package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import org.jetbrains.annotations.NotNull;

/**
 * Shared by the elements drawn with object components: player heads and sprites.
 * <p>
 * The client draws an object component 8 pixels wide and high, from one pixel above its text line, and
 * moves the cursor by 8. Its position cannot be changed vertically, so these elements are placed on a text
 * line instead of a y. Servers and clients older than 1.21.9 do not know object components: they get nothing.
 */
final class ObjectElements {

    static final int SIZE = 8;
    private static final MinecraftVersion OBJECT_COMPONENTS = MinecraftVersion.parse("1.21.9");
    private static final boolean SERVER_SUPPORT = hasObjectComponents();

    private ObjectElements() {
    }

    private static boolean hasObjectComponents() {
        try {
            Class.forName("net.kyori.adventure.text.object.ObjectContents");
            return MinecraftVersion.getCurrentVersion().isAtLeast(OBJECT_COMPONENTS);
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    static boolean supported(@NotNull CanvasRenderContext context) {
        return SERVER_SUPPORT && context.plugin().getClientVersionManager().getClientVersion(context.player()).isAtLeast(OBJECT_COMPONENTS);
    }
}
