package fr.maxlego08.menu.zcore.logger;

import fr.maxlego08.menu.hooks.ComponentMeta;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ComponentLogger extends Logger {
    private final ComponentMeta componentMeta;

    public ComponentLogger(@NotNull String prefix,@NotNull ComponentMeta componentMeta) {
        super(prefix);

        this.componentMeta = componentMeta;
        LogType.setIsAdventure(true);
    }

    @Override
    public void log(@NotNull String message, @NotNull LogType type, Object... args) {
        Bukkit.getConsoleSender().sendMessage(this.componentMeta.getComponent("<dark_gray>[<yellow>" + this.prefix + "</yellow>] </dark_gray><yellow>" + type.getColor() + this.getColoredMessage(String.format(message, args))));
    }
}
