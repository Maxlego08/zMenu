package fr.maxlego08.menu.hooks.dialogs.canvas.text;

import com.google.gson.JsonArray;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.SortedMap;

/**
 * A font canvas text can be drawn with. The pack holds one copy of the font for every vertical position
 * a text is drawn at, since a font provider has a single vertical position.
 */
public interface CanvasTextFont {

    /**
     * @return The name of the font in {@code resource-pack/fonts.yml}, or {@code default}.
     */
    @NotNull
    String name();

    /**
     * @return True if the font can be measured and generated.
     */
    boolean isReady();

    /**
     * @param codepoint A character.
     * @return How far the client moves the cursor after drawing it, without bold.
     */
    double advance(int codepoint);

    /**
     * @return The distance between two lines of a text written with this font.
     */
    int lineHeight();

    /**
     * @param y The canvas y of the top of the text.
     * @return The providers of the copy of this font drawn at {@code y}.
     */
    @NotNull
    JsonArray providers(int y);

    /**
     * @return The files this font needs in the pack, by path inside the pack.
     */
    @NotNull
    SortedMap<String, byte[]> files();

    /**
     * @param name The name of a font.
     * @param y    The canvas y of the top of the text.
     * @return The key of the copy of the font drawn at {@code y}.
     */
    static @NotNull Key fontKey(@NotNull String name, int y) {
        return Key.key("zmenu", "text/" + name + "/y" + y);
    }
}
