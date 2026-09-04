package fr.maxlego08.menu.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Paper-Dispatch attaches an executor to every required argument node, so a command declaring
 * several required arguments is also executable when only the first ones are typed. The command
 * then runs with arguments that were never parsed and Brigadier throws
 * {@code No such argument '...' exists on this command}.
 * <p>
 * Only the last required argument may be executable, so this class removes the executor from all
 * the previous ones just before the node is built. Brigadier then answers with its usual
 * "incomplete command" error instead of running the command.
 */
public abstract class ZSubCommand<T extends Plugin> extends SubCommand<T> {

    private final List<ArgumentBuilder<CommandSourceStack, ?>> requiredArgumentBuilders = new ArrayList<>();

    protected ZSubCommand(T plugin, String name) {
        super(plugin, name);
    }

    protected ZSubCommand(T plugin, String name, String... aliases) {
        super(plugin, name, aliases);
    }

    @Override
    protected void addRequiredArgument(ArgumentBuilder<CommandSourceStack, ?> argument, ArgumentExecutor<T> executor) {
        super.addRequiredArgument(argument, executor);
        // Paper-Dispatch may wrap the builder before this point, so only the instance received here
        // is the one that will end up in the command tree.
        this.requiredArgumentBuilders.add(argument);
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> build() {
        for (int index = 0; index < this.requiredArgumentBuilders.size() - 1; index++) {
            this.requiredArgumentBuilders.get(index).executes(null);
        }
        return super.build();
    }
}
