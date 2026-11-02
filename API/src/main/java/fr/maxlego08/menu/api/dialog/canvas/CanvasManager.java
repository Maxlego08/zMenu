package fr.maxlego08.menu.api.dialog.canvas;

import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Draws dialog canvases: images, text and click areas placed at pixel positions inside a dialog
 * {@code plain_message} body, using fonts zMenu generates into a resource pack.
 * <p>
 * Glyph sources are registered in {@link fr.maxlego08.menu.api.registry.Registries#GLYPH_PROVIDER}, the
 * ways to deliver the pack in {@link fr.maxlego08.menu.api.registry.Registries#CANVAS_PACK_TARGET}, and
 * element types in {@link fr.maxlego08.menu.api.registry.Registries#CANVAS_ELEMENT}.
 */
public interface CanvasManager {

    /**
     * Resolves a glyph reference such as {@code nexo:menu_items}, {@code itemsadder:my_pack:coin} or
     * {@code zmenu:shop/button}. A reference without a provider is read as a zMenu texture.
     *
     * @param reference The reference.
     * @return The glyph, or an empty result if its provider is missing or does not know it.
     */
    @NotNull
    Optional<ResolvedGlyph> resolveGlyph(@NotNull String reference);

    /**
     * Starts a new canvas, drawn with the fonts of the last generated pack.
     *
     * @param width  The width in GUI pixels, from 1 to 1014.
     * @param height The height in GUI pixels.
     * @return The canvas.
     */
    @NotNull
    CanvasBuilder newCanvas(int width, int height);

    /**
     * Starts a new canvas for one player, so its click areas work with the player's client version.
     *
     * @param width  The width in GUI pixels, from 1 to 1014.
     * @param height The height in GUI pixels.
     * @param viewer The player the canvas is drawn for.
     * @return The canvas.
     */
    @NotNull
    CanvasBuilder newCanvas(int width, int height, @NotNull Player viewer);

    /**
     * Creates the click event of a canvas area. The action runs on the player's thread, only for the player
     * the canvas was drawn for, as many times as the player clicks, until the next {@link #resetClicks(Player)}
     * (the next dialog opened for the player) or until {@code dialog-canvas.click-lifetime-seconds} have passed.
     *
     * @param viewer The player the canvas is drawn for.
     * @param action What a click does.
     * @return The click event.
     */
    @NotNull
    ClickEvent callback(@NotNull Player viewer, @NotNull Consumer<Player> action);

    /**
     * Forgets the click areas of the canvases drawn for a player before: their clicks stop working. Called
     * each time a dialog is opened for the player, before its canvases are drawn.
     *
     * @param viewer The player.
     */
    void resetClicks(@NotNull Player viewer);

    /**
     * @param type The {@code type} of an element.
     * @return Its loader, or an empty result for an unknown type.
     */
    @NotNull
    Optional<CanvasElementLoader> getElementLoader(@NotNull String type);

    /**
     * Generates the resource pack again from the loaded dialogs, then hands it to the active target.
     *
     * @param reason Why the pack is generated, for the console.
     */
    void regenerate(@NotNull String reason);

    /**
     * Generates the resource pack again for a plugin that is building its own pack right now and imports
     * zMenu's files into it. Unlike {@link #regenerate(String)}, this never asks that plugin to rebuild,
     * and can be called from any thread.
     *
     * @param reason Why the pack is generated, for the console.
     * @return The files, or an empty result if the generation failed.
     */
    @NotNull
    Optional<GeneratedPack> generateForPackBuild(@NotNull String reason);

    /**
     * @return The files of the last generation, or an empty result before the first one.
     */
    @NotNull
    Optional<GeneratedPack> getGeneratedPack();

    /**
     * @return The target the pack is delivered with, or an empty result when the server owner adds it by
     * hand.
     */
    @NotNull
    Optional<CanvasPackTarget> getActiveTarget();

    /**
     * Tells whether a player is shown canvases or their fallback, following
     * {@code dialog-canvas.require-pack}: {@code auto} trusts the pack plugin unless a pack failed for the
     * player, {@code true} needs a pack reported as loaded, {@code false} always shows canvases. Bedrock
     * players never see canvases.
     *
     * @param player The player.
     * @return True if the canvas is shown to the player.
     */
    boolean canSeeCanvas(@NotNull Player player);

    /**
     * Reports what a player's client answered to a resource pack. Bukkit only reports packs sent while the
     * player is in the world: packs sent during the configuration phase, such as CraftEngine's or the one of
     * {@code server.properties}, are reported here by the packet hooks that can read them.
     *
     * @param player The player.
     * @param pack   The id of the pack the client answered for, or null when the client sent none.
     * @param loaded True if the pack loaded, false if it was declined or failed.
     */
    void reportResourcePack(@NotNull UUID player, @Nullable UUID pack, boolean loaded);

    /**
     * The answer of a player's client, kept per pack so the order of the answers does not matter. When zMenu
     * sends its own pack, only the answer for that pack counts; otherwise zMenu cannot tell which pack holds
     * its files, so one declined or failed pack is enough to show the fallback.
     *
     * @param player The player.
     * @return True if the packs were reported loaded, false if one was declined or failed, or an empty result
     * when nothing was reported.
     */
    @NotNull
    Optional<Boolean> getResourcePackStatus(@NotNull UUID player);

    /**
     * Bedrock players, through Geyser and Floodgate, cannot load a Java resource pack, so they always get
     * the fallback of a canvas.
     *
     * @param player The player.
     * @return True if the player plays on Bedrock Edition.
     */
    boolean isBedrockPlayer(@NotNull Player player);

    /**
     * The problems found in the canvases when the pack was last generated: missing glyphs, unknown fonts,
     * elements outside their canvas, fixed text that does not fit its box.
     *
     * @return The problems, by dialog file name, in the order of the dialogs.
     */
    @NotNull
    Map<String, List<String>> getProblems();

    /**
     * @return The state of canvas widgets, kept in memory only.
     */
    @NotNull
    CanvasStateService getStateService();

    /**
     * Opens a dialog again to show a change made by a click on its canvas, keeping the widget values that last
     * until the dialog is opened from outside.
     *
     * @param player The player.
     * @param dialog The dialog file name, or {@code <plugin>:<file name>}.
     */
    void reopen(@NotNull Player player, @NotNull String dialog);

    /**
     * @return The {@code resource-pack} folder of zMenu, holding the source textures and fonts.
     */
    @NotNull
    Path getResourcePackFolder();
}
