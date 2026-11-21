package fr.maxlego08.menu.api.utils.record.bedrock;

import fr.maxlego08.menu.api.text.TextService;
import fr.robie.minimessageflow.Template;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An option of a Bedrock dropdown: its name, compiled once.
 */
public final class BedrockSimpleOption {

    private final Template optionName;
    private final boolean initialValue;

    public BedrockSimpleOption(@NotNull String optionName, boolean initialValue) {
        Objects.requireNonNull(optionName, "optionName cannot be null");
        this.optionName = TextService.get().flow().compile(optionName);
        this.initialValue = initialValue;
    }

    /**
     * @return the name shown for the option
     */
    public @NotNull Template optionNameTemplate() {
        return this.optionName;
    }

    public boolean initialValue() {
        return this.initialValue;
    }

    /**
     * @return the name as written
     * @deprecated render {@link #optionNameTemplate()}; its {@code source()} is the name as written.
     */
    @Deprecated(since = "1.1.2.2")
    public @NotNull String optionName() {
        return this.optionName.source();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof BedrockSimpleOption option && this.initialValue == option.initialValue
                && this.optionName.source().equals(option.optionName.source());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.optionName.source(), this.initialValue);
    }

    @Override
    public String toString() {
        return "BedrockSimpleOption[optionName=" + this.optionName.source() + ", initialValue=" + this.initialValue + "]";
    }
}
