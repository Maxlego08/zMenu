package fr.maxlego08.menu.zcore.utils.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * A functional interface that represents a {@link List} of {@link String} consumers.
 * It takes two parameters: a {@link CommandSender} and an array of {@link String}.
 * It is used to process the arguments of a command.
 */
@FunctionalInterface
public interface CollectionBiConsumer {


    /**
     * Process the arguments of a command.
     *
     * @param sender the {@link CommandSender} that sent the command
     * @param args   the arguments of the command
     * @return a {@link List} of {@link String} that contains the result of the processing
     */
    List<String> accept(CommandSender sender, String[] args);

}
