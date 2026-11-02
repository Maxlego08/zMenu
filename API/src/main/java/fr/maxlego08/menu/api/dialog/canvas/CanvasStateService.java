package fr.maxlego08.menu.api.dialog.canvas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * The state of the widgets of dialog canvases: values by player, dialog file name and key, kept in memory
 * only. They are never written to zMenu's player data: a server that wants a value to last uses a widget's
 * {@code default} and {@code on-change} to keep it where it wants.
 * <p>
 * Inside a dialog, a value is the placeholder {@code %canvas_<key>%}; anywhere else it is
 * {@code %zmenu_canvas_<dialog>:<key>%}.
 */
public interface CanvasStateService {

    /**
     * How long a value is kept.
     */
    enum Keep {
        /**
         * Until the dialog is opened again from outside (a command, an action, a dialog list): kept while the
         * canvas re-opens the dialog after a click.
         */
        OPEN,
        /**
         * Until the player leaves or zMenu reloads.
         */
        SESSION;

        /**
         * @param value {@code open} or {@code session}, in any case.
         * @return The lifetime, {@link #OPEN} for anything else.
         */
        public static @NotNull Keep parse(@Nullable String value) {
            return value != null && value.trim().toUpperCase(Locale.ROOT).equals("SESSION") ? SESSION : OPEN;
        }
    }

    /**
     * @param player The player.
     * @param dialog The dialog file name.
     * @param key    The key.
     * @return The value, or null when it is not set.
     */
    @Nullable
    String get(@NotNull UUID player, @NotNull String dialog, @NotNull String key);

    /**
     * @param player The player.
     * @param dialog The dialog file name.
     * @param key    The key.
     * @param value  The value.
     * @param keep   How long it is kept.
     */
    void set(@NotNull UUID player, @NotNull String dialog, @NotNull String key, @NotNull String value, @NotNull Keep keep);

    /**
     * Forgets a value.
     *
     * @param player The player.
     * @param dialog The dialog file name.
     * @param key    The key.
     */
    void remove(@NotNull UUID player, @NotNull String dialog, @NotNull String key);

    /**
     * @param player The player.
     * @param dialog The dialog file name.
     * @return Every value of the dialog for the player, by key.
     */
    @NotNull
    Map<String, String> all(@NotNull UUID player, @NotNull String dialog);

    /**
     * @param player The player.
     * @return Every value of the player, by dialog file name then key.
     */
    @NotNull
    Map<String, Map<String, String>> all(@NotNull UUID player);

    /**
     * Says that the next opening of the dialog is the canvas showing a change, so the values kept
     * {@link Keep#OPEN} survive it.
     *
     * @param player The player.
     * @param dialog The dialog file name.
     */
    void markReopen(@NotNull UUID player, @NotNull String dialog);

    /**
     * Forgets a {@link #markReopen(UUID, String)} the opening did not use, when the dialog did not open (a
     * requirement failed, the event was cancelled...), so a later opening from outside still forgets the values
     * kept {@link Keep#OPEN}.
     *
     * @param player The player.
     * @param dialog The dialog file name.
     */
    void cancelReopen(@NotNull UUID player, @NotNull String dialog);

    /**
     * Called when a dialog opens: unless {@link #markReopen(UUID, String)} was called just before, the values
     * kept {@link Keep#OPEN} are forgotten.
     *
     * @param player The player.
     * @param dialog The dialog file name.
     */
    void onOpen(@NotNull UUID player, @NotNull String dialog);

    /**
     * Forgets every value of a player.
     *
     * @param player The player.
     */
    void clear(@NotNull UUID player);

    /**
     * Forgets every value of every player.
     */
    void clearAll();
}
