package fr.maxlego08.menu.api.configuration.dialog;

public class ConfigDialogBuilder {
    private final String name;
    private final String externalTitle;
    private String yesText = "<green>Confirm";
    private String yesTooltip = "";
    private int yesWidth = 150;
    private String noText = "<red>Cancel";
    private String noTooltip = "";
    private int noWidth  = 150;
    private String booleanConfirmText = "<white>%key% :</white> %value% <gray>|</gray> %text%";
    private String numberRangeConfirmText= "<white>%key% :</white> %value%";
    private String textConfirmText="<white>%key% : <gray>%text%";

    public ConfigDialogBuilder(String name, String externalTitle) {
        this.name = name;
        this.externalTitle = externalTitle;
    }

    public ConfigDialogBuilder yesText(String yesText) {
        this.yesText = yesText;
        return this;
    }

    public ConfigDialogBuilder yesTooltip(String yesTooltip) {
        this.yesTooltip = yesTooltip;
        return this;
    }

    public ConfigDialogBuilder yesWidth(int yesWidth) {
        this.yesWidth = yesWidth;
        return this;
    }

    public ConfigDialogBuilder noText(String noText) {
        this.noText = noText;
        return this;
    }

    public ConfigDialogBuilder noTooltip(String noTooltip) {
        this.noTooltip = noTooltip;
        return this;
    }

    public ConfigDialogBuilder noWidth(int noWidth) {
        this.noWidth = noWidth;
        return this;
    }

    public ConfigDialogBuilder booleanConfirmText(String booleanConfirmText) {
        this.booleanConfirmText = booleanConfirmText;
        return this;
    }

    public ConfigDialogBuilder numberRangeConfirmText(String numberRangeConfirmText) {
        this.numberRangeConfirmText = numberRangeConfirmText;
        return this;
    }

    public ConfigDialogBuilder textConfirmText(String textConfirmText) {
        this.textConfirmText = textConfirmText;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getExternalTitle() {
        return externalTitle;
    }

    public String getYesText() {
        return yesText;
    }

    public String getYesTooltip() {
        return yesTooltip;
    }

    public int getYesWidth() {
        return yesWidth;
    }

    public String getNoText() {
        return noText;
    }

    public String getNoTooltip() {
        return noTooltip;
    }

    public int getNoWidth() {
        return noWidth;
    }

    public String getBooleanConfirmText() {
        return booleanConfirmText;
    }

    public String getNumberRangeConfirmText() {
        return numberRangeConfirmText;
    }

    public String getTextConfirmText() {
        return textConfirmText;
    }
}
