package fr.maxlego08.menu.command;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.Arguments;
import fr.maxlego08.menu.zcore.utils.commands.CollectionBiConsumer;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import fr.maxlego08.menu.zcore.utils.commands.Tab;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class VCommand extends Arguments {

    protected final MenuPlugin plugin;
    /**
     * Mother command of this command
     */
    protected VCommand parent;
    protected List<VCommand> subVCommands = new ArrayList<VCommand>();
    protected boolean runAsync = false;
    /**
     * This is the person who executes the command
     */
    protected CommandSender sender;
    protected Player player;
    protected Map<Integer, CollectionBiConsumer> tabCompletions = new HashMap<>();
    /**
     * Permission used for the command, if it is a null then everyone can
     * execute the command
     */
    private String permission;
    private String denyMessage;
    /**
     * Are all sub commands used
     */
    private final List<String> subCommands = new ArrayList<String>();
    private final List<String> requireArgs = new ArrayList<String>();
    private final List<String> optionalArgs = new ArrayList<String>();
    /**
     * If this variable is false the command will not be able to use this
     * command
     */
    private boolean consoleCanUse = true;
    /**
     * This variable allows to run the main class of the command even with
     * arguments convenient for commands like /ban <player>
     */
    private boolean ignoreParent = false;
    private boolean ignoreArgs = false;
    private boolean extendedArgs = false;
    private CommandType tabCompleter = CommandType.DEFAULT;
    private String syntax;
    private String description;
    private int argsMinLength;
    private int argsMaxLength;

    /**
     * @param plugin
     */
    public VCommand(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    //
    // GETTER
    //

    public Optional<CollectionBiConsumer> getCompletionAt(int index) {
        return Optional.ofNullable(this.tabCompletions.getOrDefault(index, null));
    }

    /**
     * Return command permission
     *
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @param permission the permission to set
     */
    protected VCommand setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * @param permission the permission to set
     */
    protected VCommand setPermission(Permission permission) {
        this.permission = permission.getPermission();
        return this;
    }

    /**
     * @return the parent
     */
    public VCommand getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    protected VCommand setParent(VCommand parent) {
        this.parent = parent;
        return this;
    }

    /**
     * @return the subCommand
     */
    public List<String> getSubCommands() {
        return subCommands;
    }

    /**
     * @return the consoleCanUse
     */
    public boolean isConsoleCanUse() {
        return consoleCanUse;
    }

    /**
     * @param consoleCanUse the consoleCanUse to set
     */
    protected VCommand setConsoleCanUse(boolean consoleCanUse) {
        this.consoleCanUse = consoleCanUse;
        return this;
    }

    /**
     * @return the ignoreParent
     */
    public boolean isIgnoreParent() {
        return ignoreParent;
    }

    public void setIgnoreParent(boolean ignoreParent) {
        this.ignoreParent = ignoreParent;
    }

    public CommandSender getSender() {
        return sender;
    }

    /**
     * @return the argsMinLength
     */
    public int getArgsMinLength() {
        return argsMinLength;
    }

    /**
     * @return the argsMaxLength
     */
    public int getArgsMaxLength() {
        return argsMaxLength;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    //
    // SETTER
    //

    /**
     * Return the generate or custom syntax
     *
     * @return the syntax
     */
    public String getSyntax() {
        if (syntax == null) {
            syntax = generateDefaultSyntax("");
        }
        return syntax;
    }

    /**
     * @param syntax the syntax to set
     */
    protected VCommand setSyntax(String syntax) {
        this.syntax = syntax;
        return this;
    }

    public boolean isIgnoreArgs() {
        return ignoreArgs;
    }

    public void setIgnoreArgs(boolean ignoreArgs) {
        this.ignoreArgs = ignoreArgs;
    }

    public String getDescription() {
        return description == null ? "no description" : description;
    }

    /**
     * Mettre la description de la commande
     *
     * @param description
     * @return
     */
    protected VCommand setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Mettre la description de la commande
     *
     * @param description
     * @return
     */
    protected VCommand setDescription(Message description) {
        this.description = description.getMessage();
        return this;
    }

    public CommandType getTabCompleter() {
        return tabCompleter;
    }

    /*
     *
     */
    protected void setTabCompletor() {
        this.tabCompleter = CommandType.SUCCESS;
    }

    /*
     * Ajouter un argument obligatoire
     */
    protected void addRequireArg(String message) {
        this.requireArgs.add(message);
        this.ignoreParent = this.parent == null;
        this.ignoreArgs = true;
    }

    /*
     * Ajouter un argument obligatoire
     */
    protected void addRequireArg(String message, CollectionBiConsumer runnable) {
        this.addRequireArg(message);
        int index = this.requireArgs.size();
        this.addCompletion(index - 1, runnable);
    }

    /**
     * Ajouter un argument optionel
     *
     * @param message
     */
    protected void addOptionalArg(String message) {
        this.optionalArgs.add(message);
        this.ignoreParent = this.parent == null;
        this.ignoreArgs = true;
    }

    /**
     * Ajouter un argument optionel
     *
     * @param message
     */
    protected void addOptionalArg(String message, CollectionBiConsumer runnable) {
        this.addOptionalArg(message);
        int index = this.requireArgs.size() + this.optionalArgs.size();
        this.addCompletion(index - 1, runnable);
    }

    /**
     * @return first command
     */
    public String getFirst() {
        return this.subCommands.get(0);
    }

    //
    // OTHER
    //

    public void setExtendedArgs(boolean extendedArgs) {
        this.extendedArgs = extendedArgs;
    }

    /**
     * Adds sub orders
     *
     * @param subCommand
     * @return this
     */
    public VCommand addSubCommand(String subCommand) {
        this.subCommands.add(subCommand);
        return this;
    }

    /**
     * Adds sub orders
     *
     * @param command
     * @return this
     */
    public VCommand addSubCommand(VCommand command) {
        command.setParent(this);
        this.plugin.getVCommandManager().registerCommand(command);
        this.subVCommands.add(command);
        return this;
    }

    /**
     * Adds sub orders
     *
     * @param subCommand
     * @return this
     */
    public VCommand addSubCommand(String... subCommand) {
        this.subCommands.addAll(Arrays.asList(subCommand));
        return this;
    }

    /**
     * Add a {@link CollectionBiConsumer} to the index for the tab completion
     *
     * @param index
     * @param runnable
     */
    public void addCompletion(int index, CollectionBiConsumer runnable) {
        this.tabCompletions.put(index, runnable);
        this.setTabCompletor();
    }

    /**
     * Allows you to generate the syntax of the command manually But you you can
     * set it yourself with the setSyntax()
     *
     * @param syntax
     * @return generate syntax
     */
    private String generateDefaultSyntax(String syntax) {

        String tmpString = subCommands.get(0);

        boolean update = syntax.equals("");

        if (requireArgs.size() != 0 && update) {
            for (String requireArg : requireArgs) {
                requireArg = "<" + requireArg + ">";
                syntax += " " + requireArg;
            }
        }
        if (optionalArgs.size() != 0 && update) {
            for (String optionalArg : optionalArgs) {
                optionalArg = "[<" + optionalArg + ">]";
                syntax += " " + optionalArg;
            }
        }

        tmpString += syntax;

        if (parent == null) {
            return "/" + tmpString;
        }

        return parent.generateDefaultSyntax(" " + tmpString);
    }

    /**
     * Allows to know the number of parents in a recursive way
     *
     * @param defaultParent
     * @return
     */
    private int parentCount(int defaultParent) {
        return parent == null ? defaultParent : parent.parentCount(defaultParent + 1);
    }

    /**
     * Allows you to manage the arguments and check that the command is valid
     *
     * @param plugin
     * @param commandSender
     * @param args
     * @return
     */
    public CommandType prePerform(MenuPlugin plugin, CommandSender commandSender, String[] args) {

        // We update the number of arguments according to the number of parents

        this.parentCount = this.parentCount(0);
        this.argsMaxLength = this.requireArgs.size() + this.optionalArgs.size() + this.parentCount;
        this.argsMinLength = this.requireArgs.size() + this.parentCount;

        // We generate the basic syntax if it is impossible to find it
        if (this.syntax == null) {
            this.syntax = generateDefaultSyntax("");
        }

        this.args = args;

        String defaultString = super.argAsString(0);

        if (defaultString != null) {
            for (VCommand subCommand : subVCommands) {
                if (subCommand.getSubCommands().contains(defaultString.toLowerCase()))
                    return CommandType.CONTINUE;
            }
        }

        if ((this.argsMinLength != 0 && args.length < this.argsMinLength)
                || this.argsMaxLength != 0 && args.length > this.argsMaxLength && !this.extendedArgs) {
            return CommandType.SYNTAX_ERROR;
        }

        this.sender = commandSender;
        if (this.sender instanceof Player) {
            this.player = (Player) commandSender;
        }

        try {
            return perform(plugin);
        } catch (Exception e) {
            if (Config.enableDebug)
                e.printStackTrace();
            return CommandType.SYNTAX_ERROR;
        }
    }

    /**
     * Method that allows you to execute the command
     */
    protected abstract CommandType perform(MenuPlugin plugin);

    public boolean sameSubCommands() {
        if (this.parent == null) {
            return false;
        }
        for (String command : this.subCommands) {
            if (this.parent.getSubCommands().contains(command))
                return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VCommand [permission=" + permission + ", subCommands=" + subCommands + ", consoleCanUse="
                + consoleCanUse + ", description=" + description + "]";
    }

    /**
     * Generate tab completion
     *
     * @param plugin
     * @param sender
     * @param args
     * @return
     */
    public List<String> toTab(MenuPlugin plugin, CommandSender sender, String[] args) {

        this.parentCount = this.parentCount(0);

        int currentInex = (args.length - this.parentCount) - 1;
        Optional<CollectionBiConsumer> optional = this.getCompletionAt(currentInex);

        if (optional.isPresent()) {

            CollectionBiConsumer collectionRunnable = optional.get();
            String startWith = args[args.length - 1];
            return this.generateList(collectionRunnable.accept(sender, args), startWith);

        }

        return null;
    }

    /**
     * Generate list for tab completer
     *
     * @param startWith
     * @param strings
     * @return
     */
    protected List<String> generateList(String startWith, String... strings) {
        return generateList(Arrays.asList(strings), startWith);
    }

    /**
     * Generate list for tab completer
     *
     * @param startWith
     * @param strings
     * @return list of string
     */
    protected List<String> generateList(Tab tab, String startWith, String... strings) {
        return generateList(Arrays.asList(strings), startWith, tab);
    }

    /**
     * Generate list for tab completer
     *
     * @param defaultList default value
     * @param startWith   tabulation star with
     * @return list of string
     */
    protected List<String> generateList(List<String> defaultList, String startWith) {
        return generateList(defaultList, startWith, Tab.CONTAINS);
    }

    /**
     * Generate list for tab completer
     *
     * @param defaultList default value
     * @param startWith   tabulation star with
     * @param tab         Tab type
     * @return list of string
     */
    protected List<String> generateList(List<String> defaultList, String startWith, Tab tab) {
        List<String> newList = new ArrayList<>();
        for (String str : defaultList) {
            if (startWith.length() == 0
                    || (tab.equals(Tab.START) ? str.toLowerCase().startsWith(startWith.toLowerCase())
                    : str.toLowerCase().contains(startWith.toLowerCase()))) {
                newList.add(str);
            }
        }
        return newList.size() == 0 ? null : newList;
    }

    /**
     * Add list of aliases
     *
     * @param aliases - Commands aliases
     */
    public void addSubCommand(List<String> aliases) {
        this.subCommands.addAll(aliases);
    }

    /**
     * Send a list of commands with their syntax and description
     */
    protected void sendSyntax() {
        message(this.sender, Message.DOCUMENTATION_INFORMATION);
        this.subVCommands.forEach(command -> {
            if (command.getPermission() == null || this.sender.hasPermission(command.getPermission())) {
                message(this.sender, Message.COMMAND_SYNTAX_HELP, "%syntax%", command.getSyntax(), "%description%",
                        command.getDescription());
            }
        });
    }

    protected Optional<Inventory> findInventory(String inventoryName, InventoryManager inventoryManager){
        Optional<Inventory> optional;
        if (inventoryName.contains(":")) {
            String[] values = inventoryName.split(":");
            if (values.length == 2) {
                optional = inventoryManager.getInventory(values[0], values[1]);
            } else {
                optional = inventoryManager.getInventory(inventoryName);
            }
        } else {
            optional = inventoryManager.getInventory(inventoryName);
        }
        return optional;
    }

    public void setDenyMessage(String denyMessage) {
        this.denyMessage = denyMessage;
    }

    public String getDenyMessage() {
        return denyMessage;
    }
}
