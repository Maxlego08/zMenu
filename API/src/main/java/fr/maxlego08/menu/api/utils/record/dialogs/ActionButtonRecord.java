package fr.maxlego08.menu.api.utils.record.dialogs;

import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import fr.robie.minimessageflow.Template;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A dialog's action button: its label and tooltip compiled once, its width and its action.
 */
public final class ActionButtonRecord {

    private final Template label;
    private final Template tooltip;
    private final int width;
    private final ZDialogAction action;

    public ActionButtonRecord(@NotNull String label, @NotNull String tooltip, int width, @NotNull ZDialogAction action) {
        this(TextService.get().flow().compile(Objects.requireNonNull(label, "label cannot be null")),
                TextService.get().flow().compile(Objects.requireNonNull(tooltip, "tooltip cannot be null")), width, action);
    }

    private ActionButtonRecord(@NotNull Template label, @NotNull Template tooltip, int width, @NotNull ZDialogAction action) {
        this.label = label;
        this.tooltip = tooltip;
        this.width = width;
        this.action = Objects.requireNonNull(action, "action cannot be null");
    }

    /**
     * @return the button's text
     */
    public @NotNull Template labelTemplate() {
        return this.label;
    }

    /**
     * @return the text shown when the button is hovered; its source is empty for none
     */
    public @NotNull Template tooltipTemplate() {
        return this.tooltip;
    }

    public int width() {
        return this.width;
    }

    public @NotNull ZDialogAction action() {
        return this.action;
    }

    /**
     * @param label the new label as written
     * @return this button with another label, the rest kept as it is
     */
    public @NotNull ActionButtonRecord withLabel(@NotNull String label) {
        return new ActionButtonRecord(TextService.get().flow().compile(label), this.tooltip, this.width, this.action);
    }

    /**
     * @param tooltip the new tooltip as written
     * @return this button with another tooltip, the rest kept as it is
     */
    public @NotNull ActionButtonRecord withTooltip(@NotNull String tooltip) {
        return new ActionButtonRecord(this.label, TextService.get().flow().compile(tooltip), this.width, this.action);
    }

    /**
     * @param width the new width
     * @return this button with another width, the rest kept as it is
     */
    public @NotNull ActionButtonRecord withWidth(int width) {
        return new ActionButtonRecord(this.label, this.tooltip, width, this.action);
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

    /**
     * @return a copy whose label and tooltip have PlaceholderAPI's placeholders replaced for the player
     * @deprecated render {@link #labelTemplate()} and {@link #tooltipTemplate()} for the player instead: they read
     * PlaceholderAPI and the dialog's own values.
     */
    @Deprecated(since = "1.1.2.2")
    public @NotNull ActionButtonRecord parse(@NotNull Player player) {
        return new ActionButtonRecord(parsePlaceholder(this.label.source(), player), parsePlaceholder(this.tooltip.source(), player), this.width, this.action);
    }

    private static String parsePlaceholder(@NotNull String text, @NotNull Player player) {
        return text.isEmpty() ? "" : PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof ActionButtonRecord button && this.width == button.width
                && this.label.source().equals(button.label.source()) && this.tooltip.source().equals(button.tooltip.source())
                && this.action.equals(button.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.label.source(), this.tooltip.source(), this.width, this.action);
    }

    @Override
    public String toString() {
        return "ActionButtonRecord[label=" + this.label.source() + ", tooltip=" + this.tooltip.source() + ", width=" + this.width + ", action=" + this.action + "]";
    }
}
