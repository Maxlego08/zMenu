package fr.maxlego08.menu.api.utils.record.dialogs;

import fr.maxlego08.menu.api.text.TextService;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.TextTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An option of a {@code single_option} dialog input: its id, read as text, and its display, compiled once.
 */
public final class SingleOption {

    private final TextTemplate id;
    private final Template display;
    private final boolean initialValue;

    public SingleOption(@NotNull String id, @NotNull String display, boolean initialValue) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(display, "display cannot be null");
        this.id = TextService.get().flow().compileText(id);
        this.display = TextService.get().flow().compile(display);
        this.initialValue = initialValue;
    }

    /**
     * @return the id sent back when the option is chosen, its placeholders only read
     */
    public @NotNull TextTemplate idTemplate() {
        return this.id;
    }

    /**
     * @return the text shown for the option
     */
    public @NotNull Template displayTemplate() {
        return this.display;
    }

    public boolean initialValue() {
        return this.initialValue;
    }

    /**
     * @return the id as written
     * @deprecated render {@link #idTemplate()}; its {@code source()} is the id as written.
     */
    @Deprecated(since = "1.1.2.2")
    public @NotNull String id() {
        return this.id.source();
    }

    /**
     * @return the display as written
     * @deprecated render {@link #displayTemplate()}; its {@code source()} is the display as written.
     */
    @Deprecated(since = "1.1.2.2")
    public @NotNull String display() {
        return this.display.source();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof SingleOption option && this.initialValue == option.initialValue
                && this.id.source().equals(option.id.source()) && this.display.source().equals(option.display.source());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id.source(), this.display.source(), this.initialValue);
    }

    @Override
    public String toString() {
        return "SingleOption[id=" + this.id.source() + ", display=" + this.display.source() + ", initialValue=" + this.initialValue + "]";
    }
}
