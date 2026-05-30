package fr.maxlego08.menu.zcore.logger;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class BukkitLogger extends Logger {

    public BukkitLogger(@NotNull String prefix) {
        super(prefix);
    }

    @Override
    public void log(@NotNull String message, @NotNull LogType type, @NonNull @NotNull Object... args) {
        Bukkit.getConsoleSender().sendMessage("§8[§e" + this.prefix + "§8] " + type.getColor() + this.getColoredMessage(String.format(message, args)));
    }
}
