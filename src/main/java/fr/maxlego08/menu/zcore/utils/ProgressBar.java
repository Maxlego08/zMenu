package fr.maxlego08.menu.zcore.utils;

public record ProgressBar(int lenght, char symbol, String completedColor, String notCompletedColor) {

    /**
     * @param lenght
     * @param symbol
     * @param completedColor
     * @param notCompletedColor
     */
    public ProgressBar {
    }

    /**
     * @return the lenght
     */
    @Override
    public int lenght() {
        return lenght;
    }

    /**
     * @return the symbol
     */
    @Override
    public char symbol() {
        return symbol;
    }

    /**
     * @return the completedColor
     */
    @Override
    public String completedColor() {
        return completedColor;
    }

    /**
     * @return the notCompletedColor
     */
    @Override
    public String notCompletedColor() {
        return notCompletedColor;
    }

}
