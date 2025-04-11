package fr.maxlego08.menu.button;

import fr.maxlego08.menu.api.button.PerformButton;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.save.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public abstract class ZPerformButton extends ZSlotButton implements PerformButton {

    private List<String> commands = new ArrayList<>();
    private List<String> leftCommands = new ArrayList<>();
    private List<String> rightCommands = new ArrayList<>();
    private List<String> consoleCommands = new ArrayList<>();
    private List<String> consoleRightCommands = new ArrayList<>();
    private List<String> consoleLeftCommands = new ArrayList<>();
    private List<String> consolePermissionCommands = new ArrayList<>();
    private String consolePermission ;

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

    public List<String> getLeftCommands() {
        return this.leftCommands;
    }


    public List<String> getRightCommands() {
        return this.rightCommands;
    }
    
    /**
     * @param leftCommands the left click commands to set
     */
    public void setLeftCommands(List<String> leftCommands) {
        this.leftCommands = leftCommands;
    }

    /**
     * @param rightCommands the right click commands to set
     */
    public void setRightCommands(List<String> rightCommands) {
        this.rightCommands = rightCommands;
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
    public void execute(Player player, ClickType type, ZScheduler scheduler, Placeholders placeholders) {
        if (type.isRightClick()) {
            this.execute(player, player, this.rightCommands, scheduler, placeholders);
            this.execute(player, player, this.consoleRightCommands, scheduler, placeholders);
        }

        if (type.isLeftClick()) {
            this.execute(player, player, this.leftCommands, scheduler, placeholders);
            this.execute(player, player, this.consoleLeftCommands, scheduler, placeholders);
        }

        this.execute(player, player, this.commands, scheduler, placeholders);
        this.execute(Bukkit.getConsoleSender(), player, this.consoleCommands, scheduler, placeholders);

        if (this.consolePermission == null || player.hasPermission(this.consolePermission)) {
            this.execute(Bukkit.getConsoleSender(), player, this.consolePermissionCommands, scheduler, placeholders);
        }
    }

    /**
     * Executes a list of commands on behalf of the specified executor and player, using a scheduler to run the commands.
     *
     * @param executor  the CommandSender executing the commands
     * @param player    the Player for whom the commands are executed
     * @param strings   the list of commands to be executed
     * @param scheduler the ZScheduler used to schedule the command executions
     */
    private void execute(CommandSender executor, Player player, List<String> strings, ZScheduler scheduler, Placeholders placeholders) {
        strings.forEach(command -> {
            command = placeholders.parse(command.replace("%player%", player.getName()));
            try {
                if (executor instanceof Player && Config.enablePlayerCommandInChat) {
                    player.chat("/" + papi(command, player, true));
                } else {

                    String finalCommand = command;
                    Runnable runnable = () -> Bukkit.dispatchCommand(executor, papi(finalCommand, player, true));
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
