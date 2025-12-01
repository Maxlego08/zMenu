package fr.maxlego08.menu.api.utils;

/**
 * Enum representing the server platform type.
 * Used to differentiate between Paper (native Adventure) and Spigot (requires wrapper).
 */
public enum PlatformType {
    /**
     * Paper server with native Adventure API support.
     */
    PAPER,

    /**
     * Spigot server requiring Adventure Platform wrapper.
     */
    SPIGOT;

    private static PlatformType detectedType;

    /**
     * Detects the server platform type by checking for Paper-specific classes.
     *
     * @return The detected platform type
     */
    public static PlatformType detect() {
        if (detectedType == null) {
            try {
                // Try to load a Paper-specific class
                Class.forName("io.papermc.paper.text.PaperComponents");
                detectedType = PAPER;
            } catch (ClassNotFoundException e) {
                detectedType = SPIGOT;
            }
        }
        return detectedType;
    }

    /**
     * Gets the detected platform type.
     * If detection hasn't been run yet, it will be performed.
     *
     * @return The platform type
     */
    public static PlatformType get() {
        return detect();
    }

    /**
     * Checks if the current platform is Paper.
     *
     * @return true if running on Paper, false otherwise
     */
    public static boolean isPaper() {
        return get() == PAPER;
    }

    /**
     * Checks if the current platform is Spigot.
     *
     * @return true if running on Spigot, false otherwise
     */
    public static boolean isSpigot() {
        return get() == SPIGOT;
    }
}