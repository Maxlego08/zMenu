package fr.maxlego08.menu.api.configuration.dialog;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    @Contract("_ -> this")
    @NotNull
    public ConfigDialogBuilder yesText(@NotNull String yesText) {
        this.yesText = yesText;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ConfigDialogBuilder yesTooltip(@NotNull String yesTooltip) {
        this.yesTooltip = yesTooltip;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ConfigDialogBuilder yesWidth(int yesWidth) {
        this.yesWidth = yesWidth;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ConfigDialogBuilder noText(@NotNull String noText) {
        this.noText = noText;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ConfigDialogBuilder noTooltip(@NotNull String noTooltip) {
        this.noTooltip = noTooltip;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ConfigDialogBuilder noWidth(int noWidth) {
        this.noWidth = noWidth;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ConfigDialogBuilder booleanConfirmText(@NotNull String booleanConfirmText) {
        this.booleanConfirmText = booleanConfirmText;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ConfigDialogBuilder numberRangeConfirmText(@NotNull String numberRangeConfirmText) {
        this.numberRangeConfirmText = numberRangeConfirmText;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public ConfigDialogBuilder textConfirmText(@NotNull String textConfirmText) {
        this.textConfirmText = textConfirmText;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public String getName() {
        return this.name;
    }

    @Contract(pure = true)
    @NotNull
    public String getExternalTitle() {
        return this.externalTitle;
    }

    @Contract(pure = true)
    @NotNull
    public String getYesText() {
        return this.yesText;
    }

    @Contract(pure = true)
    @NotNull
    public String getYesTooltip() {
        return this.yesTooltip;
    }

    @Contract(pure = true)
    public int getYesWidth() {
        return this.yesWidth;
    }

    @Contract(pure = true)
    @NotNull
    public String getNoText() {
        return this.noText;
    }

    @Contract(pure = true)
    @NotNull
    public String getNoTooltip() {
        return this.noTooltip;
    }

    @Contract(pure = true)
    public int getNoWidth() {
        return this.noWidth;
    }

    @Contract(pure = true)
    @NotNull
    public String getBooleanConfirmText() {
        return this.booleanConfirmText;
    }

    @Contract(pure = true)
    @NotNull
    public String getNumberRangeConfirmText() {
        return this.numberRangeConfirmText;
    }

    @Contract(pure = true)
    @NotNull
    public String getTextConfirmText() {
        return this.textConfirmText;
    }
}
