package fr.maxlego08.menu.api.button.dialogs;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class InputButton extends Button {
    private DialogInputType inputType = DialogInputType.BOOLEAN;
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


    public DialogInputType getInputType() {
        return inputType;
    }

    public InputButton setInputType(DialogInputType inputType) {
        this.inputType = inputType;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public InputButton setLabel(String label) {
        this.label = label;
        return this;
    }

    public boolean isLabelVisible() {
        return labelVisible;
    }

    public InputButton setLabelVisible(boolean labelVisible) {
        this.labelVisible = labelVisible;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public InputButton setWidth(int width) {
        this.width = width;
        return this;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public InputButton setDefaultText(String defaultText) {
        this.defaultText = defaultText;
        return this;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public InputButton setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public int getMultilineMaxLines() {
        return multilineMaxLines;
    }

    public InputButton setMultilineMaxLines(int multilineMaxLines) {
        this.multilineMaxLines = multilineMaxLines;
        return this;
    }

    public int getMultilineHeight() {
        return multilineHeight;
    }

    public InputButton setMultilineHeight(int multilineHeight) {
        this.multilineHeight = multilineHeight;
        return this;
    }

    public List<SingleOption> getSigleOptions() {
        return singleOptions;
    }

    public InputButton setSigleOptions(List<SingleOption> options) {
        this.singleOptions = options;
        return this;
    }

    public String getInitialValueBool() {
        return initialValueBool;
    }

    public InputButton setInitialValueBool(String initialValueBool) {
        this.initialValueBool = initialValueBool;
        return this;
    }

    public String getTextTrue() {
        return textTrue;
    }

    public InputButton setTextTrue(String textTrue) {
        this.textTrue = textTrue;
        return this;
    }

    public String getTextFalse() {
        return textFalse;
    }

    public InputButton setTextFalse(String textFalse) {
        this.textFalse = textFalse;
        return this;
    }

    public float getStart() {
        return start;
    }

    public InputButton setStart(float start) {
        this.start = start;
        return this;
    }

    public float getEnd() {
        return end;
    }

    public InputButton setEnd(float end) {
        this.end = end;
        return this;
    }

    public float getStep() {
        return step;
    }

    public InputButton setStep(float step) {
        this.step = step;
        return this;
    }

    public String getInitialValueRange() {
        return initialValueRange;
    }

    public InputButton setInitialValueRange(String initialValueRange) {
        this.initialValueRange = initialValueRange;
        return this;
    }

    public String getKey() {
        return key;
    }

    public InputButton setKey(String key) {
        this.key = key;
        return this;
    }

    public String getLabelFormat() {
        return labelFormat;
    }
    public InputButton setLabelFormat(String labelFormat) {
        this.labelFormat = labelFormat;
        return this;
    }
    public Optional<Boolean> getInitialValueSupplier() {
        if (this.initialValueSupplier != null) {;
            return Optional.ofNullable(initialValueSupplier.get());
        }
        return Optional.empty();
    }
    public InputButton setInitialValueSupplier(Supplier<Boolean> initialValueSupplier) {
        this.initialValueSupplier = initialValueSupplier;
        return this;
    }

    public Optional<Float> getInitialValueRangeSupplier() {
        if (this.initialValueRangeSupplier != null) {;
            return Optional.ofNullable(initialValueRangeSupplier.get());
        }
        return Optional.empty();
    }

    public InputButton setInitialValueRangeSupplier(Supplier<Float> initialValueRangeSupplier) {
        this.initialValueRangeSupplier = initialValueRangeSupplier;
        return this;
    }
    public Optional<String> getDefaultTextSupplier() {
        if (this.defaultTextSupplier != null) {;
            return Optional.ofNullable(defaultTextSupplier.get());
        }
        return Optional.empty();
    }
    public InputButton setDefaultTextSupplier(Supplier<String> defaultTextSupplier) {
        this.defaultTextSupplier = defaultTextSupplier;
        return this;
    }
}

