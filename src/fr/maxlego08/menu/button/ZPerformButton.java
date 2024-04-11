package fr.maxlego08.menu.button;

import fr.maxlego08.menu.api.button.PerformButton;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.save.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public abstract class ZPerformButton extends ZSlotButton implements PerformButton {

    private List<String> commands;
    private List<String> consoleCommands;
    private List<String> consoleRightCommands;
    private List<String> consoleLeftCommands;
    private List<String> consolePermissionCommands;
    private String consolePermission;

    @Override
    public List<String> getCommands() {
        return this.commands;
    }

    /**
     * @param commands the commands to set
     */
    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public List<String> getConsoleCommands() {
        return this.consoleCommands;
    }

    /**
     * @param consoleCommands the consoleCommands to set
     */
    public void setConsoleCommands(List<String> consoleCommands) {
        this.consoleCommands = consoleCommands;
    }

    @Override
    public List<String> getConsolePermissionCommands() {
        return this.consolePermissionCommands;
    }

    /**
     * @param consolePermissionCommands the consolePermissionCommands to set
     */
    public void setConsolePermissionCommands(List<String> consolePermissionCommands) {
        this.consolePermissionCommands = consolePermissionCommands;
    }

    @Override
    public String getConsolePermission() {
        return this.consolePermission;
    }

    /**
     * @param consolePermission the consolePermission to set
     */
    public void setConsolePermission(String consolePermission) {
        this.consolePermission = consolePermission;
    }

    @Override
    public List<String> getConsoleRightCommands() {
        return this.consoleRightCommands;
    }

    /**
     * @param consoleRightCommands the consoleRightCommands to set
     */
    public void setConsoleRightCommands(List<String> consoleRightCommands) {
        this.consoleRightCommands = consoleRightCommands;
    }

    @Override
    public List<String> getConsoleLeftCommands() {
        return this.consoleLeftCommands;
    }

    /**
     * @param consoleLeftCommands the consoleLeftCommands to set
     */
    public void setConsoleLeftCommands(List<String> consoleLeftCommands) {
        this.consoleLeftCommands = consoleLeftCommands;
    }

    @Override
    public void execute(Player player, ClickType type, ZScheduler scheduler) {

        scheduler.runTask(player.getLocation(), () -> {
            if (type.equals(ClickType.RIGHT)) {
                this.execute(player, player, this.consoleRightCommands, scheduler);
            }

            if (type.equals(ClickType.LEFT)) {
                this.execute(player, player, this.consoleLeftCommands, scheduler);
            }

            this.execute(player, player, this.commands, scheduler);
            this.execute(Bukkit.getConsoleSender(), player, this.consoleCommands, scheduler);

            if (this.consolePermission == null || player.hasPermission(this.consolePermission)) {
                this.execute(Bukkit.getConsoleSender(), player, this.consolePermissionCommands, scheduler);
            }
        });
    }

    /**
     * Allows you to execute a list of commands
     *
     * @param executor
     * @param player
     * @param strings
     */
    private void execute(CommandSender executor, Player player, List<String> strings, ZScheduler scheduler) {
        strings.forEach(command -> {
            command = command.replace("%player%", player.getName());
            try {
                if (executor instanceof Player && Config.enablePlayerCommandInChat) {
                    player.chat("/" + papi(command, player));
                } else {

                    String finalCommand = command;
                    if (executor instanceof Player) {
                        scheduler.runTask(((Player) executor).getLocation(), () -> Bukkit.dispatchCommand(executor, papi(finalCommand, player)));
                    } else {
                        scheduler.runTask(null, () -> Bukkit.dispatchCommand(executor, papi(finalCommand, player)));
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
