package fr.maxlego08.menu.api.button.dialogs;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class InputButton extends Button {
    private final DialogInputType inputType;
    private String key; // Unique identifier for the input button
    private String label;

    private boolean labelVisible = true;

    // Text input properties
    private int width = 200;
    private String defaultText = "";
    private int maxLength = 32; // Default max length for text input
    private int multilineMaxLines; // Optional positive integer. If present, limits maximum lines.
    private int multilineHeight; // Value between 1 and 512 — Height of input.
    private Supplier<String> defaultTextSupplier;

    // Single option input properties
    private List<SingleOption> singleOptions;

    // Don't use Label Visible

    // Boolean input properties
    private String initialValueBool = String.valueOf(true);
    private Supplier<Boolean> initialValueSupplier;
    private String textTrue = "";
    private String textFalse = "";

    // Number range input properties
    // use width
    private float start = (float) 0; // Default start value
    private float end = (float) 100; // Default end value
    private float step = (float) 1; // Default step value
    private String initialValueRange = String.valueOf(50); // Default initial value
    private Supplier<Float> initialValueRangeSupplier;
    private String labelFormat = "options.generic_value"; // Default label format

    public InputButton(DialogInputType inputType) {
        super();
        this.inputType = inputType;
    }

    @Contract(pure = true)
    public DialogInputType getInputType() {
        return inputType;
    }

    public String getLabel() {
        return label;
    }

    @Contract("_ -> this")
    public InputButton setLabel(@Nullable String label) {
        this.label = label;
        return this;
    }

    @Contract(pure = true)
    public boolean isLabelVisible() {
        return labelVisible;
    }

    @Contract("_ -> this")
    public InputButton setLabelVisible(boolean labelVisible) {
        this.labelVisible = labelVisible;
        return this;
    }

    @Contract(pure = true)
    public int getWidth() {
        return width;
    }

    @Contract("_ -> this")
    public InputButton setWidth(int width) {
        this.width = width;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public String getDefaultText() {
        return defaultText;
    }

    @Contract("_ -> this")
    public InputButton setDefaultText(@NotNull String defaultText) {
        this.defaultText = defaultText;
        return this;
    }

    @Contract(pure = true)
    public int getMaxLength() {
        return maxLength;
    }

    @Contract("_ -> this")
    public InputButton setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    @Contract(pure = true)
    public int getMultilineMaxLines() {
        return multilineMaxLines;
    }

    @Contract("_ -> this")
    public InputButton setMultilineMaxLines(int multilineMaxLines) {
        this.multilineMaxLines = multilineMaxLines;
        return this;
    }

    @Contract(pure = true)
    public int getMultilineHeight() {
        return multilineHeight;
    }

    @Contract("_ -> this")
    public InputButton setMultilineHeight(int multilineHeight) {
        this.multilineHeight = multilineHeight;
        return this;
    }

    @Contract(pure = true)
    @Nullable
    public List<SingleOption> getSigleOptions() {
        return singleOptions;
    }

    @Contract("_ -> this")
    public InputButton setSigleOptions(@Nullable List<SingleOption> options) {
        this.singleOptions = options;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public String getInitialValueBool() {
        return initialValueBool;
    }

    @Contract("_ -> this")
    public InputButton setInitialValueBool(@NotNull String initialValueBool) {
        this.initialValueBool = initialValueBool;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public String getTextTrue() {
        return textTrue;
    }

    @Contract("_ -> this")
    public InputButton setTextTrue(@NotNull String textTrue) {
        this.textTrue = textTrue;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public String getTextFalse() {
        return textFalse;
    }

    @Contract("_ -> this")
    public InputButton setTextFalse(@NotNull String textFalse) {
        this.textFalse = textFalse;
        return this;
    }

    @Contract(pure = true)
    public float getStart() {
        return start;
    }

    @Contract("_ -> this")
    public InputButton setStart(float start) {
        this.start = start;
        return this;
    }

    @Contract(pure = true)
    public float getEnd() {
        return end;
    }

    @Contract("_ -> this")
    public InputButton setEnd(float end) {
        this.end = end;
        return this;
    }

    @Contract(pure = true)
    public float getStep() {
        return step;
    }

    @Contract("_ -> this")
    public InputButton setStep(float step) {
        this.step = step;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public String getInitialValueRange() {
        return initialValueRange;
    }

    @Contract("_ -> this")
    public InputButton setInitialValueRange(@NotNull String initialValueRange) {
        this.initialValueRange = initialValueRange;
        return this;
    }

    @Contract(pure = true)
    @Nullable
    public String getKey() {
        return key;
    }

    @Contract("_ -> this")
    public InputButton setKey(@Nullable String key) {
        this.key = key;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public String getLabelFormat() {
        return labelFormat;
    }

    @Contract("_ -> this")
    public InputButton setLabelFormat(@NotNull String labelFormat) {
        this.labelFormat = labelFormat;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public Optional<Boolean> getInitialValueSupplier() {
        if (this.initialValueSupplier != null) {
            return Optional.ofNullable(initialValueSupplier.get());
        }
        return Optional.empty();
    }

    @Contract("_ -> this")
    public InputButton setInitialValueSupplier(@Nullable Supplier<Boolean> initialValueSupplier) {
        this.initialValueSupplier = initialValueSupplier;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public Optional<Float> getInitialValueRangeSupplier() {
        if (this.initialValueRangeSupplier != null) {
            return Optional.ofNullable(initialValueRangeSupplier.get());
        }
        return Optional.empty();
    }

    @Contract("_ -> this")
    public InputButton setInitialValueRangeSupplier(@Nullable Supplier<Float> initialValueRangeSupplier) {
        this.initialValueRangeSupplier = initialValueRangeSupplier;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public Optional<String> getDefaultTextSupplier() {
        if (this.defaultTextSupplier != null) {
            return Optional.ofNullable(defaultTextSupplier.get());
        }
        return Optional.empty();
    }

    @Contract("_ -> this")
    public InputButton setDefaultTextSupplier(@Nullable Supplier<String> defaultTextSupplier) {
        this.defaultTextSupplier = defaultTextSupplier;
        return this;
    }
}
