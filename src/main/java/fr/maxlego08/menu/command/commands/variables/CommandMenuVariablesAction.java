package fr.maxlego08.menu.command.commands.variables;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.common.utils.command.NonSpaceStringArgumentType;
import fr.maxlego08.menu.variables.ZVariableService;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

/**
 * One action of {@code /zm variables}, on a {@code server} or {@code global} variable.
 */
public class CommandMenuVariablesAction extends SubCommand<ZMenuPlugin> {

    private final Action action;

    public CommandMenuVariablesAction(ZMenuPlugin plugin, Action action) {
        super(plugin, action.name().toLowerCase(Locale.ROOT));
        this.action = action;
        this.setPermission(action.permission.getPermission());

        this.addRequiredArgument(Commands.argument("scope", StringArgumentType.word()).suggests((ctx, builder) -> {
            for (String scope : new String[]{"server", "global"}) {
                if (scope.startsWith(builder.getRemainingLowerCase())) builder.suggest(scope);
            }
            return builder.buildFuture();
        }));

        if (action == Action.KEYS) return;

        this.addRequiredArgument(Commands.argument("key", new NonSpaceStringArgumentType()).suggests((ctx, builder) -> {
            VariableScope scope = scope(ctx.getArgument("scope", String.class));
            if (scope != null) {
                this.plugin.getVariableService().names(scope).stream().filter(name -> name.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase())).forEach(builder::suggest);
            }
            return builder.buildFuture();
        }));

        switch (action) {
            case SET -> {
                this.addRequiredArgument(Commands.argument("expire-after", IntegerArgumentType.integer(0)).suggests((ctx, builder) -> {
                    builder.suggest("0").suggest("60").suggest("300").suggest("3600").suggest("86400");
                    return builder.buildFuture();
                }));
                this.addRequiredArgument(Commands.argument("value", StringArgumentType.greedyString()));
            }
            case ADD, SUBTRACT -> this.addRequiredArgument(Commands.argument("number", StringArgumentType.word()).suggests((ctx, builder) -> {
                builder.suggest("1").suggest("5").suggest("10").suggest("100");
                return builder.buildFuture();
            }));
            default -> {
            }
        }
    }

    /**
     * @return the scope a command names, only {@code server} and {@code global}
     */
    private static VariableScope scope(String name) {
        VariableScope scope = VariableScope.parse(name);
        return scope == VariableScope.PLAYER ? null : scope;
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        CommandSender sender = commandDispatch.getSender();
        ZVariableService variables = this.plugin.getVariableService();

        String scopeName = commandDispatch.getArgument("scope", String.class);
        VariableScope scope = scope(scopeName);
        if (scope == null) {
            MessageUtils.message(this.plugin, sender, Message.VARIABLES_SCOPE_ERROR, "%scope%", scopeName);
            return CommandResultType.SUCCESS;
        }

        if (this.action == Action.KEYS) {
            ArrayList<String> names = new ArrayList<>(variables.names(scope));
            if (names.isEmpty()) {
                MessageUtils.message(this.plugin, sender, Message.VARIABLES_KEYS_EMPTY, "%scope%", scope.id());
            } else {
                names.sort(String::compareTo);
                MessageUtils.message(this.plugin, sender, Message.VARIABLES_KEYS_SUCCESS, "%scope%", scope.id(), "%keys%", ZUtils.toList(names, "§8", "§7"));
            }
            return CommandResultType.SUCCESS;
        }

        String key = commandDispatch.getArgument("key", String.class);
        VariableRef ref = variables.ref(scope, null, key);

        switch (this.action) {
            case GET -> {
                Optional<Variable> optional = variables.getCached(ref);
                if (optional.isEmpty()) {
                    MessageUtils.message(this.plugin, sender, Message.VARIABLES_GET_ERROR, "%scope%", scope.id(), "%key%", key);
                } else {
                    Variable variable = optional.get();
                    MessageUtils.message(this.plugin, sender, Message.VARIABLES_GET_SUCCESS, "%scope%", scope.id(), "%key%", key, "%value%", variable.text(), "%expiredAt%", variable.expiresAt());
                }
            }
            case SET -> {
                int expireAfter = commandDispatch.getArgument("expire-after", Integer.class);
                String value = commandDispatch.getArgument("value", String.class);
                long expiresAt = expireAfter <= 0 ? 0 : System.currentTimeMillis() + 1000L * expireAfter;
                variables.set(ref, value, expiresAt);
                MessageUtils.message(this.plugin, sender, Message.VARIABLES_SET, "%scope%", scope.id(), "%key%", key, "%value%", value);
            }
            case ADD, SUBTRACT -> {
                String number = commandDispatch.getArgument("number", String.class);
                BigDecimal amount = Variable.parseNumber(number);
                if (amount == null) {
                    MessageUtils.message(this.plugin, sender, Message.VARIABLES_NUMBER_ERROR, "%value%", number);
                    return CommandResultType.SUCCESS;
                }
                variables.increment(ref, this.action == Action.ADD ? amount : amount.negate(), null).whenComplete((value, throwable) -> {
                    if (throwable == null) this.plugin.getScheduler().runNextTick(task -> MessageUtils.message(this.plugin, sender, Message.VARIABLES_CHANGED,
                            "%scope%", scope.id(), "%key%", key, "%value%", Variable.format(value)));
                });
            }
            case REMOVE -> {
                variables.delete(ref);
                MessageUtils.message(this.plugin, sender, Message.VARIABLES_REMOVE, "%scope%", scope.id(), "%key%", key);
            }
            default -> {
            }
        }
        return CommandResultType.SUCCESS;
    }

    public enum Action {
        GET(Permission.ZMENU_VARIABLES_GET),
        SET(Permission.ZMENU_VARIABLES_SET),
        ADD(Permission.ZMENU_VARIABLES_ADD),
        SUBTRACT(Permission.ZMENU_VARIABLES_SUBTRACT),
        REMOVE(Permission.ZMENU_VARIABLES_REMOVE),
        KEYS(Permission.ZMENU_VARIABLES_KEYS);

        private final Permission permission;

        Action(Permission permission) {
            this.permission = permission;
        }
    }
}
