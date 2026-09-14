package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class ZActionPlayerData extends ZUtils implements ActionPlayerData {

    private final StorageManager storageManager;
    private final String key;
    private final ActionPlayerDataType type;
    private final Object value;
    private final String seconds;
    private final boolean enableMathExpression;

    public ZActionPlayerData(StorageManager storageManager, String key, ActionPlayerDataType type, Object value, String seconds, boolean enableMathExpression) {
        super();
        this.storageManager = storageManager;
        this.key = key;
        this.type = type;
        this.value = value;
        this.seconds = seconds;
        this.enableMathExpression = enableMathExpression;
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
    public @NonNull Data toData(OfflinePlayer player) {
        return this.toData(player, new Placeholders());
    }

    @Override
    public @NonNull Data toData(OfflinePlayer player, @NonNull Placeholders placeholders) {
        long seconds;
        try {
            seconds = Long.parseLong(this.papi(this.seconds,player,false));
        } catch (Exception e) {
            seconds = 0;
        }
        long expiredAt = seconds == 0 ? 0 : System.currentTimeMillis() + (1000 * seconds);
        String result = placeholders.parse(this.papi(this.value.toString(), player, false));
        String dataValue = result;
        if (this.enableMathExpression) {
            Integer evaluated = this.evaluate(result);
            if (evaluated != null) dataValue = String.valueOf(evaluated.intValue());
        }
        return new ZData(this.papi(this.key, player, false), dataValue, expiredAt);
    }

    /**
     * Resolves the value to add or subtract.
     * <p>
     * The value goes through placeholders, so at runtime it can be an unresolved
     * {@code %placeholder%}, an empty string or a decimal. Neither the expression engine nor
     * {@link Integer#parseInt(String)} may throw here: the action runs on a click, and an
     * exception would silently skip every action queued after it.
     *
     * @return the resolved amount, or {@code null} when the value is not usable.
     */
    private Integer evaluate(String result) {
        try {
            return this.enableMathExpression
                    ? (int) new ExpressionBuilder(result).build().evaluate()
                    : (int) Double.parseDouble(result.trim().replace(",", "."));
        } catch (Exception exception) {
            Logger.info("Unable to use " + result + " as a number for the player data " + this.key + ": "
                    + exception.getMessage(), Logger.LogType.ERROR);
            return null;
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ZActionPlayerData [key=" + this.key + ", type=" + this.type + ", value=" + this.value + ", seconds=" + this.seconds + "]";
    }

    @Override
    public void execute(@NonNull Player player, @NonNull DataManager dataManager) {
        this.execute(player, dataManager, new Placeholders());
    }

    @Override
    public void execute(@NonNull Player player, @NonNull DataManager dataManager, @NonNull Placeholders placeholders) {
        if (this.type == ActionPlayerDataType.REMOVE) {

            Optional<PlayerData> optional = dataManager.getPlayer(player.getUniqueId());
            optional.ifPresent(data -> data.removeData(this.papi(this.key, player, false)));
        } else if (this.type == ActionPlayerDataType.ADD) {

            Optional<Data> optional = dataManager.getData(player.getUniqueId(), this.papi(this.key, player, false));
            if (optional.isPresent()) {
                Data data = optional.get();
                String result = placeholders.parse(this.papi(this.value.toString(), player, false));
                Integer amount = this.evaluate(result);
                if (amount == null) return;
                data.add(amount);
                this.storageManager.upsertData(player.getUniqueId(), data);
            } else {
                dataManager.addData(player.getUniqueId(), this.toData(player,placeholders));
            }
        } else if (this.type == ActionPlayerDataType.SUBTRACT) {

            Optional<Data> optional = dataManager.getData(player.getUniqueId(), this.papi(this.key, player, false));
            if (optional.isPresent()) {
                Data data = optional.get();
                String result = placeholders.parse(this.papi(this.value.toString(), player, false));
                Integer amount = this.evaluate(result);
                if (amount == null) return;
                data.remove(amount);
                this.storageManager.upsertData(player.getUniqueId(), data);
            } else {
                var data = this.toData(player,placeholders);
                data.negate();
                dataManager.addData(player.getUniqueId(), data);
            }
        } else if (this.type == ActionPlayerDataType.SET) {
            dataManager.addData(player.getUniqueId(), this.toData(player,placeholders));
        }
    }
}
