package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public abstract class PerformButton extends SlotButton {

    private List<String> commands = new ArrayList<>();
    private List<String> leftCommands = new ArrayList<>();
    private List<String> rightCommands = new ArrayList<>();
    private List<String> consoleCommands = new ArrayList<>();
    private List<String> consoleRightCommands = new ArrayList<>();
    private List<String> consoleLeftCommands = new ArrayList<>();
    private List<String> consolePermissionCommands = new ArrayList<>();
    private String consolePermission;

    public List<String> getCommands() {
        return this.commands;
    }

    /**
     * @param commands the commands to set
     */
    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public List<String> getLeftCommands() {
        return this.leftCommands;
    }

    /**
     * @param leftCommands the left click commands to set
     */
    public void setLeftCommands(List<String> leftCommands) {
        this.leftCommands = leftCommands;
    }

    public List<String> getRightCommands() {
        return this.rightCommands;
    }

    /**
     * @param rightCommands the right click commands to set
     */
    public void setRightCommands(List<String> rightCommands) {
        this.rightCommands = rightCommands;
    }


    public List<String> getConsoleCommands() {
        return this.consoleCommands;
    }

    /**
     * @param consoleCommands the consoleCommands to set
     */
    public void setConsoleCommands(List<String> consoleCommands) {
        this.consoleCommands = consoleCommands;
    }

    public List<String> getConsolePermissionCommands() {
        return this.consolePermissionCommands;
    }

    /**
     * @param consolePermissionCommands the consolePermissionCommands to set
     */
    public void setConsolePermissionCommands(List<String> consolePermissionCommands) {
        this.consolePermissionCommands = consolePermissionCommands;
    }

    public String getConsolePermission() {
        return this.consolePermission;
    }

    /**
     * @param consolePermission the consolePermission to set
     */
    public void setConsolePermission(String consolePermission) {
        this.consolePermission = consolePermission;
    }

    public List<String> getConsoleRightCommands() {
        return this.consoleRightCommands;
    }

    /**
     * @param consoleRightCommands the consoleRightCommands to set
     */
    public void setConsoleRightCommands(List<String> consoleRightCommands) {
        this.consoleRightCommands = consoleRightCommands;
    }

    public List<String> getConsoleLeftCommands() {
        return this.consoleLeftCommands;
    }

    /**
     * @param consoleLeftCommands the consoleLeftCommands to set
     */
    public void setConsoleLeftCommands(List<String> consoleLeftCommands) {
        this.consoleLeftCommands = consoleLeftCommands;
    }

    public void execute(MenuPlugin plugin, ClickType type, Placeholders placeholders, Player player) {
        ZScheduler scheduler = plugin.getScheduler();

        if (type.isRightClick()) {
            this.execute(plugin, player, this.rightCommands, scheduler, placeholders, player);
            this.execute(plugin, player, this.consoleRightCommands, scheduler, placeholders, player);
        }

        if (type.isLeftClick()) {
            this.execute(plugin, player, this.leftCommands, scheduler, placeholders, player);
            this.execute(plugin, player, this.consoleLeftCommands, scheduler, placeholders, player);
        }

        this.execute(plugin, player, this.commands, scheduler, placeholders, player);
        this.execute(plugin, player, this.consoleCommands, scheduler, placeholders, Bukkit.getConsoleSender());

        if (this.consolePermission == null || player.hasPermission(this.consolePermission)) {
            this.execute(plugin, player, this.consolePermissionCommands, scheduler, placeholders, Bukkit.getConsoleSender());
        }
    }

    /**
     * Executes a list of commands on behalf of the specified executor and player, using a scheduler to run the commands.
     *
     * @param plugin    the plugin
     * @param player    the Player for whom the commands are executed
     * @param strings   the list of commands to be executed
     * @param scheduler the ZScheduler used to schedule the command executions
     * @param executor  the CommandSender executing the commands
     */
    private void execute(MenuPlugin plugin, Player player, List<String> strings, ZScheduler scheduler, Placeholders placeholders, CommandSender executor) {
        strings.forEach(command -> {
            command = placeholders.parse(command.replace("%player%", player.getName()));
            try {
                if (executor instanceof Player && Config.enablePlayerCommandInChat) {
                    player.chat("/" + plugin.parse(player, command));
                } else {

                    String finalCommand = command;
                    Runnable runnable = () -> Bukkit.dispatchCommand(executor, plugin.parse(player, finalCommand));
                    if (scheduler.isFolia()) {
                        if (executor instanceof Player) scheduler.runTask(((Player) executor).getLocation(), runnable);
                        else scheduler.runTask(null, runnable);
                    } else runnable.run();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
