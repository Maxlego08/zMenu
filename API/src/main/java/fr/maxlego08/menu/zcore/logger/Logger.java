package fr.maxlego08.menu.zcore.logger;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public abstract class Logger {
    protected final String prefix;
    private static Logger logger;

    protected Logger(@NotNull String prefix) {
        Preconditions.checkNotNull(prefix, "Prefix cannot be null");
        this.prefix = prefix;
        logger = this;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void info(String message, LogType type) {
        getLogger().log(message, type);
    }

    public static void info(String message) {
        getLogger().log(message, LogType.INFO);
    }

    public static void info(String message, Object... args) {
        getLogger().log(message, args);
    }

    public static void info(String message, LogType type, Object... args) {
        getLogger().log(message, type, args);
    }

    public static void error(String message) {
        getLogger().log(message, LogType.ERROR);
    }

    public void log(String message) {
        this.log(message, LogType.INFO);
    }

    public void log(String message, Object... args) {
        this.log(message, LogType.INFO, args);
    }

    public abstract void log(@NotNull String message,@NotNull LogType type, @NotNull Object... args);

    public void log(@NotNull String[] messages,@NotNull LogType type) {
        for (String message : messages) {
            this.log(message, type);
        }
    }

    public String getColoredMessage(String message) {
        return message.replace("<&>", "§");
    }

    public enum LogType {
        ERROR("§c", "<red>"),
        INFO("§7", "<gray>"),
        WARNING("§6", "<yellow>"),
        SUCCESS("§2", "<green>");
        private static boolean isAdventure = false;

        private final String color;
        private final String adventureColorCode;

        LogType(@NotNull String legacyColorCode, @NotNull String adventureColorCode) {
            this.color = legacyColorCode;
            this.adventureColorCode = adventureColorCode;
        }

        public String getColor() {
            return isAdventure ? this.adventureColorCode : this.color;
        }

        public static void setIsAdventure(boolean isAdventure) {
            LogType.isAdventure = isAdventure;
        }
    }
}
