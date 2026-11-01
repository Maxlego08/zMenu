package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.api.variable.VariableService;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * The {@code data} action: sets, adds to, subtracts from or removes a variable of the player, of
 * this server or of every server.
 */
public class ZActionPlayerData extends ZUtils implements ActionPlayerData {

    private final MenuPlugin plugin;
    private final String key;
    private final ActionPlayerDataType type;
    private final Object value;
    private final String seconds;
    private final boolean enableMathExpression;
    private final VariableScope scope;

    public ZActionPlayerData(MenuPlugin plugin, String key, ActionPlayerDataType type, Object value, String seconds, boolean enableMathExpression, VariableScope scope) {
        super();
        this.plugin = plugin;
        this.key = key;
        this.type = type;
        this.value = value;
        this.seconds = seconds;
        this.enableMathExpression = enableMathExpression;
        this.scope = scope;
    }

    @Override
    public @NonNull String getKey() {
        return this.key;
    }

    @Override
    public @NonNull ActionPlayerDataType getType() {
        return this.type;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public @NonNull String getSeconds() {
        return this.seconds;
    }

    @Override
    public boolean isMathExpression() {
        return this.enableMathExpression;
    }

    @Override
    public @NonNull VariableScope getScope() {
        return this.scope;
    }

    private long seconds(OfflinePlayer player) {
        try {
            return Long.parseLong(this.papi(this.seconds, player, false));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public @NonNull Data toData(OfflinePlayer player) {
        return this.toData(player, new Placeholders());
    }

    @Override
    public @NonNull Data toData(OfflinePlayer player, @NonNull Placeholders placeholders) {
        long seconds = this.seconds(player);
        long expiredAt = seconds == 0 ? 0 : System.currentTimeMillis() + (1000 * seconds);
        String result = placeholders.parse(this.papi(this.value.toString(), player, false));
        String dataValue = result;
        if (this.enableMathExpression) {
            BigDecimal evaluated = this.evaluate(result);
            if (evaluated != null) dataValue = Variable.format(evaluated);
        }
        return new ZData(this.papi(this.key, player, false), dataValue, expiredAt);
    }

    /**
     * Resolves the value to add or subtract.
     * <p>
     * The value goes through placeholders, so at runtime it can be an unresolved
     * {@code %placeholder%}, an empty string or a decimal. Neither the expression engine nor the
     * number parser may throw here: the action runs on a click, and an exception would silently
     * skip every action queued after it.
     *
     * @return the resolved amount, or {@code null} when the value is not usable.
     */
    private BigDecimal evaluate(String result) {
        try {
            if (this.enableMathExpression) {
                double evaluated = new ExpressionBuilder(result).build().evaluate();
                return evaluated == Math.rint(evaluated) && !Double.isInfinite(evaluated) ? BigDecimal.valueOf((long) evaluated) : BigDecimal.valueOf(evaluated);
            }
            BigDecimal number = Variable.parseNumber(result);
            if (number == null) throw new NumberFormatException("not a number");
            return number;
        } catch (Exception exception) {
            Logger.info("Unable to use " + result + " as a number for the player data " + this.key + ": "
                    + exception.getMessage(), Logger.LogType.ERROR);
            return null;
        }
    }

    @Override
    public String toString() {
        return "ZActionPlayerData [key=" + this.key + ", type=" + this.type + ", value=" + this.value + ", seconds=" + this.seconds + ", scope=" + this.scope + "]";
    }

    @Override
    public void execute(@NonNull Player player, @NonNull DataManager dataManager) {
        this.execute(player, dataManager, new Placeholders());
    }

    /**
     * Runs the action on the variable of its scope. Additions and subtractions are sent to the
     * database as such, so two servers changing the same value at once both count.
     */
    @Override
    public void execute(@NonNull Player player, @NonNull DataManager dataManager, @NonNull Placeholders placeholders) {
        VariableService variables = this.plugin.getVariableService();
        VariableRef ref = variables.ref(this.scope, player.getUniqueId(), this.papi(this.key, player, false));

        switch (this.type) {
            case REMOVE -> variables.delete(ref);
            case SET -> {
                Data data = this.toData(player, placeholders);
                variables.set(ref, data.getValue() == null ? "" : data.getValue().toString(), data.getExpiredAt());
            }
            case ADD, SUBTRACT -> {
                BigDecimal amount = this.evaluate(placeholders.parse(this.papi(this.value.toString(), player, false)));
                if (amount == null) return;
                long seconds = this.seconds(player);
                variables.increment(ref, this.type == ActionPlayerDataType.ADD ? amount : amount.negate(), seconds == 0 ? null : Duration.ofSeconds(seconds));
            }
        }
    }
}
