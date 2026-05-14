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
        ERROR("§c"),
        INFO("§7"),
        WARNING("§6"),
        SUCCESS("§2");

        private final String color;

        LogType(String color) {
            this.color = color;
        }

        public String getColor() {
            return this.color;
        }
    }
}
