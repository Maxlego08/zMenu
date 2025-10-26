package fr.maxlego08.menu.api.button.dialogs;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;

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

    public DialogInputType getInputType() {
        return inputType;
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

    public void setLabelVisible(boolean labelVisible) {
        this.labelVisible = labelVisible;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMultilineMaxLines() {
        return multilineMaxLines;
    }

    public void setMultilineMaxLines(int multilineMaxLines) {
        this.multilineMaxLines = multilineMaxLines;
    }

    public int getMultilineHeight() {
        return multilineHeight;
    }

    public void setMultilineHeight(int multilineHeight) {
        this.multilineHeight = multilineHeight;
    }

    public List<SingleOption> getSigleOptions() {
        return singleOptions;
    }

    public void setSigleOptions(List<SingleOption> options) {
        this.singleOptions = options;
    }

    public String getInitialValueBool() {
        return initialValueBool;
    }

    public void setInitialValueBool(String initialValueBool) {
        this.initialValueBool = initialValueBool;
    }

    public String getTextTrue() {
        return textTrue;
    }

    public void setTextTrue(String textTrue) {
        this.textTrue = textTrue;
    }

    public String getTextFalse() {
        return textFalse;
    }

    public void setTextFalse(String textFalse) {
        this.textFalse = textFalse;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public String getInitialValueRange() {
        return initialValueRange;
    }

    public void setInitialValueRange(String initialValueRange) {
        this.initialValueRange = initialValueRange;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabelFormat() {
        return labelFormat;
    }

    public void setLabelFormat(String labelFormat) {
        this.labelFormat = labelFormat;
    }

    public Optional<Boolean> getInitialValueSupplier() {
        if (this.initialValueSupplier != null) {;
            return Optional.ofNullable(initialValueSupplier.get());
        }
        return Optional.empty();
    }

    public void setInitialValueSupplier(Supplier<Boolean> initialValueSupplier) {
        this.initialValueSupplier = initialValueSupplier;
    }

    public Optional<Float> getInitialValueRangeSupplier() {
        if (this.initialValueRangeSupplier != null) {;
            return Optional.ofNullable(initialValueRangeSupplier.get());
        }
        return Optional.empty();
    }

    public void setInitialValueRangeSupplier(Supplier<Float> initialValueRangeSupplier) {
        this.initialValueRangeSupplier = initialValueRangeSupplier;
    }

    public Optional<String> getDefaultTextSupplier() {
        if (this.defaultTextSupplier != null) {;
            return Optional.ofNullable(defaultTextSupplier.get());
        }
        return Optional.empty();
    }

    public void setDefaultTextSupplier(Supplier<String> defaultTextSupplier) {
        this.defaultTextSupplier = defaultTextSupplier;
    }
}

