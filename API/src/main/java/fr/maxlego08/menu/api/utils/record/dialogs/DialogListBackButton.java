package fr.maxlego08.menu.api.utils.record.dialogs;

import fr.maxlego08.menu.api.text.TextService;
import fr.robie.minimessageflow.Template;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The button a {@code dialog_list} adds to the dialogs it opens, so the player can come back to the list.
 * The vanilla client closes every dialog when a dialog opened from a list is closed, so without it the
 * player has no way back.
 * <p>
 * The button only fills an empty exit slot: it is added to {@code multi_action}, {@code server_links} and
 * {@code dialog_list} dialogs that have no exit button of their own. {@code notice} and {@code confirmation}
 * dialogs have no exit slot and are left unchanged.
 * <p>
 * Its label and tooltip are compiled once and rendered for the viewer (PlaceholderAPI included).
 */
public final class DialogListBackButton {

    public static final DialogListBackButton DEFAULT = new DialogListBackButton(true, "Back", "", 150);

    private final boolean enabled;
    private final Template label;
    private final Template tooltip;
    private final int width;

    /**
     * @param enabled whether the button is added
     * @param label   the button text
     * @param tooltip the button tooltip, empty for none
     * @param width   the button width, from 1 to 1024
     */
    public DialogListBackButton(boolean enabled, @NotNull String label, @NotNull String tooltip, int width) {
        this.enabled = enabled;
        this.label = TextService.get().flow().compile(Objects.requireNonNull(label, "label cannot be null"));
        this.tooltip = TextService.get().flow().compile(Objects.requireNonNull(tooltip, "tooltip cannot be null"));
        this.width = width;
    }

    public boolean enabled() {
        return this.enabled;
    }

    /**
     * @return the button text
     */
    public @NotNull Template labelTemplate() {
        return this.label;
    }

    /**
     * @return the button tooltip; its source is empty for none
     */
    public @NotNull Template tooltipTemplate() {
        return this.tooltip;
    }

    public int width() {
        return this.width;
    }

    /**
     * @return the label as written
     * @deprecated render {@link #labelTemplate()}; its {@code source()} is the label as written.
     */
    @Deprecated(since = "1.1.2.2")
    public @NotNull String label() {
        return this.label.source();
    }

    /**
     * @return the tooltip as written
     * @deprecated render {@link #tooltipTemplate()}; its {@code source()} is the tooltip as written.
     */
    @Deprecated(since = "1.1.2.2")
    public @NotNull String tooltip() {
        return this.tooltip.source();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof DialogListBackButton button && this.enabled == button.enabled && this.width == button.width
                && this.label.source().equals(button.label.source()) && this.tooltip.source().equals(button.tooltip.source());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.enabled, this.label.source(), this.tooltip.source(), this.width);
    }

    @Override
    public String toString() {
        return "DialogListBackButton[enabled=" + this.enabled + ", label=" + this.label.source() + ", tooltip=" + this.tooltip.source()
                + ", width=" + this.width + "]";
    }
}
