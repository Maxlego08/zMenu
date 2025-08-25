package fr.maxlego08.menu.api.button.dialogs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.requirement.ViewRequirementDialogs;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;

import java.util.List;
import java.util.function.Supplier;

public class InputButton implements ViewRequirementDialogs {
    private MenuPlugin plugin;
    private DialogInputType inputType;
    private String key; // Unique identifier for the input button
    private String label;

    private boolean labelVisible = true;

    // Text input properties
    private int width = 200;
    private String defaultText = "";
    private int maxLength = 32; // Default max length for text input
    private int multilineMaxLines; // Optional positive integer. If present, limits maximum lines.
    private int multilineHeight; // Value between 1 and 512 — Height of input.

    // Single option input properties
    private List<SingleOption> options;

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
    private String labelFormat = "options.generic_value"; // Default label format

    private Requirement viewRequirement;

    public MenuPlugin getPlugin() {
        return plugin;
    }

    public InputButton setPlugin(MenuPlugin plugin) {
        this.plugin = plugin;
        return this;
    }

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

    public List<SingleOption> getOptions() {
        return options;
    }

    public InputButton setOptions(List<SingleOption> options) {
        this.options = options;
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
    public Object getInitialValueSupplier() {
        if (initialValueSupplier != null) {;
            return initialValueSupplier.get();
        }
        return null;
    }
    public InputButton setInitialValueSupplier(Supplier<Boolean> initialValueSupplier) {
        this.initialValueSupplier = initialValueSupplier;
        return this;
    }

    @Override
    public Requirement getViewRequirement() {
        return viewRequirement;
    }

    public InputButton setViewRequirement(Requirement viewRequirement) {
        this.viewRequirement = viewRequirement;
        return this;
    }
}

