package fr.maxlego08.menu.zcore.utils.toast.utils;

public enum ColorCode {
    BLACK('0', "black"),
    DARK_BLUE('1', "dark_blue"),
    DARK_GREEN('2', "dark_green"),
    DARK_AQUA('3', "dark_aqua"),
    DARK_RED('4', "dark_red"),
    DARK_PURPLE('5', "dark_purple"),
    GOLD('6', "gold"),
    GRAY('7', "gray"),
    DARK_GRAY('8', "dark_gray"),
    BLUE('9', "blue"),
    GREEN('a', "green"),
    AQUA('b', "aqua"),
    RED('c', "red"),
    LIGHT_PURPLE('d', "light_purple"),
    YELLOW('e', "yellow"),
    WHITE('f', "white");

    private final char code;
    private final String name;

    ColorCode(char code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getColorName(char code) {
        for (ColorCode c : values()) {
            if (c.code == code) {
                return c.name;
            }
        }
        return "white";
    }

    public char getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}