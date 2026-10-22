package fr.maxlego08.menu.api.utils.record.dialogs;

import org.jetbrains.annotations.NotNull;

/**
 * The button a {@code dialog_list} adds to the dialogs it opens, so the player can come back to the list.
 * The vanilla client closes every dialog when a dialog opened from a list is closed, so without it the
 * player has no way back.
 * <p>
 * The button only fills an empty exit slot: it is added to {@code multi_action}, {@code server_links} and
 * {@code dialog_list} dialogs that have no exit button of their own. {@code notice} and {@code confirmation}
 * dialogs have no exit slot and are left unchanged.
 *
 * @param enabled whether the button is added
 * @param label   the button text, placeholders are parsed for the viewer
 * @param tooltip the button tooltip, empty for none
 * @param width   the button width, from 1 to 1024
 */
public record DialogListBackButton(boolean enabled, @NotNull String label, @NotNull String tooltip, int width) {

    public static final DialogListBackButton DEFAULT = new DialogListBackButton(true, "Back", "", 150);
}
