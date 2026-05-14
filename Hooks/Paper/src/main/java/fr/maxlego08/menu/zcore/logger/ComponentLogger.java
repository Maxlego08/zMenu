package fr.maxlego08.menu.zcore.logger;

import fr.maxlego08.menu.hooks.ComponentMeta;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ComponentLogger extends Logger {
    private final ComponentMeta componentMeta;

    public ComponentLogger(@NotNull String prefix,@NotNull ComponentMeta componentMeta) {
        super(prefix);

        this.componentMeta = componentMeta;
    }

    @Override
    public void log(@NotNull String message, @NotNull LogType type, Object... args) {
        Bukkit.getConsoleSender().sendMessage(this.componentMeta.getComponent("§8[§e" + this.prefix + "§8] §e" + type.getColor() + this.getColoredMessage(String.format(message, args))));
    }
}
