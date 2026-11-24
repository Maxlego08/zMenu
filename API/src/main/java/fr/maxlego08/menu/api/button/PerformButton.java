package fr.maxlego08.menu.api.button;

import com.tcoded.folialib.impl.PlatformScheduler;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.robie.minimessageflow.TextTemplate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class PerformButton extends SlotButton {

    private List<String> commands = new ArrayList<>();
    private List<String> leftCommands = new ArrayList<>();
    private List<String> rightCommands = new ArrayList<>();
    private List<String> consoleCommands = new ArrayList<>();
    private List<String> consoleRightCommands = new ArrayList<>();
    private List<String> consoleLeftCommands = new ArrayList<>();
    private List<String> consolePermissionCommands = new ArrayList<>();
    private String consolePermission;
    /**
     * Each command list compiled once, by list, compiled again when its lines change: read as text, values placed as
     * written.
     */
    private final transient Map<List<String>, CompiledCommands> compiledCommands = Collections.synchronizedMap(new IdentityHashMap<>());

    @Contract(pure = true)
    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    /**
     * @param commands the commands to set
     */
    public void setCommands(@NotNull List<String> commands) {
        this.compiledCommands.clear();
        this.commands = commands;
    }

    @Contract(pure = true)
    @NotNull
    public List<String> getLeftCommands() {
        return this.leftCommands;
    }

    /**
     * @param leftCommands the left click commands to set
     */
    public void setLeftCommands(@NotNull List<String> leftCommands) {
        this.compiledCommands.clear();
        this.leftCommands = leftCommands;
    }

    @Contract(pure = true)
    @NotNull
    public List<String> getRightCommands() {
        return this.rightCommands;
    }

    /**
     * @param rightCommands the right click commands to set
     */
    public void setRightCommands(@NotNull List<String> rightCommands) {
        this.compiledCommands.clear();
        this.rightCommands = rightCommands;
    }

    @Contract(pure = true)
    @NotNull
    public List<String> getConsoleCommands() {
        return this.consoleCommands;
    }

    /**
     * @param consoleCommands the consoleCommands to set
     */
    public void setConsoleCommands(@NotNull List<String> consoleCommands) {
        this.compiledCommands.clear();
        this.consoleCommands = consoleCommands;
    }

    @Contract(pure = true)
    @NotNull
    public List<String> getConsolePermissionCommands() {
        return this.consolePermissionCommands;
    }

    /**
     * @param consolePermissionCommands the consolePermissionCommands to set
     */
    public void setConsolePermissionCommands(@NotNull List<String> consolePermissionCommands) {
        this.compiledCommands.clear();
        this.consolePermissionCommands = consolePermissionCommands;
    }

    @Contract(pure = true)
    @Nullable
    public String getConsolePermission() {
        return this.consolePermission;
    }

    /**
     * @param consolePermission the consolePermission to set
     */
    public void setConsolePermission(@Nullable String consolePermission) {
        this.consolePermission = consolePermission;
    }

    @Contract(pure = true)
    @NotNull
    public List<String> getConsoleRightCommands() {
        return this.consoleRightCommands;
    }

    /**
     * @param consoleRightCommands the consoleRightCommands to set
     */
    public void setConsoleRightCommands(@NotNull List<String> consoleRightCommands) {
        this.compiledCommands.clear();
        this.consoleRightCommands = consoleRightCommands;
    }

    @Contract(pure = true)
    @NotNull
    public List<String> getConsoleLeftCommands() {
        return this.consoleLeftCommands;
    }

    /**
     * @param consoleLeftCommands the consoleLeftCommands to set
     */
    public void setConsoleLeftCommands(@NotNull List<String> consoleLeftCommands) {
        this.compiledCommands.clear();
        this.consoleLeftCommands = consoleLeftCommands;
    }

    @Contract(pure = true)
    public void execute(@NotNull MenuPlugin plugin, @NotNull ClickType type, @NotNull Placeholders placeholders, @NotNull Player player) {
        var scheduler = plugin.getScheduler();

        if (type.isRightClick()) {
            this.execute(plugin, player, this.rightCommands, scheduler, placeholders, player);
            this.execute(plugin, player, this.consoleRightCommands, scheduler, placeholders, Bukkit.getConsoleSender());
        }

        if (type.isLeftClick()) {
            this.execute(plugin, player, this.leftCommands, scheduler, placeholders, player);
            this.execute(plugin, player, this.consoleLeftCommands, scheduler, placeholders, Bukkit.getConsoleSender());
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
     * @param scheduler the PlatformScheduler used to schedule the command executions
     * @param executor  the CommandSender executing the commands
     */
    @Contract(pure = true)
    private void execute(@NotNull MenuPlugin plugin, @NotNull Player player, @NotNull List<String> strings, @NotNull PlatformScheduler scheduler, @NotNull Placeholders placeholders, @NotNull CommandSender executor) {
        if (strings.isEmpty()) {
            return;
        }
        CompiledCommands compiled = this.compiledCommands.get(strings);
        if (compiled == null || !compiled.sources().equals(strings)) {
            compiled = new CompiledCommands(new ArrayList<>(strings), TextService.texts(strings));
            this.compiledCommands.put(strings, compiled);
        }
        compiled.templates().forEach(template -> {
            String command = template.render(placeholders.values(), player);
            try {
                if (executor instanceof Player && Configuration.enablePlayerCommandInChat) {
                    player.chat("/" + command);
                } else {
                    Consumer<WrappedTask> task = w -> Bukkit.dispatchCommand(executor, command);
                    if (executor instanceof Entity entity) {
                        scheduler.runAtEntity(entity, task);
                    } else {
                        scheduler.runNextTick(task);
                    }
                }
            } catch (Exception exception) {
                if (Configuration.enableDebug){
                    Logger.info("An error occurred while executing command: " + command);
                    Logger.error(exception);
                }
            }
        });
    }

    /**
     * A command list compiled, with the lines it was compiled from: a list changed in place is compiled again.
     */
    private record CompiledCommands(@NotNull List<String> sources, @NotNull List<TextTemplate> templates) {
    }
}
