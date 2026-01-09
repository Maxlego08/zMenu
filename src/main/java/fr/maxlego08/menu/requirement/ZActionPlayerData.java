package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.zcore.utils.ZUtils;
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
        return toData(player, new Placeholders());
    }

    @Override
    public @NonNull Data toData(OfflinePlayer player, @NonNull Placeholders placeholders) {
        long seconds;
        try {
            seconds = Long.parseLong(papi(this.seconds,player,false));
        } catch (Exception e) {
            seconds = 0;
        }
        long expiredAt = seconds == 0 ? 0 : System.currentTimeMillis() + (1000 * seconds);
        String result = placeholders.parse(papi(this.value.toString(), player, false));
        String dataValue = this.enableMathExpression ? String.valueOf((int) new ExpressionBuilder(result).build().evaluate()) : result;
        return new ZData(this.papi(this.key, player, false), dataValue, expiredAt);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ZActionPlayerData [key=" + key + ", type=" + type + ", value=" + value + ", seconds=" + seconds + "]";
    }

    @Override
    public void execute(@NonNull Player player, @NonNull DataManager dataManager) {
        execute(player, dataManager, new Placeholders());
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
                String result = placeholders.parse(papi(this.value.toString(), player, false));
                data.add(this.enableMathExpression ? (int) new ExpressionBuilder(result).build().evaluate() : Integer.parseInt(result));
                storageManager.upsertData(player.getUniqueId(), data);
            } else {
                dataManager.addData(player.getUniqueId(), this.toData(player,placeholders));
            }
        } else if (this.type == ActionPlayerDataType.SUBTRACT) {

            Optional<Data> optional = dataManager.getData(player.getUniqueId(), this.papi(this.key, player, false));
            if (optional.isPresent()) {
                Data data = optional.get();
                String result = placeholders.parse(papi(this.value.toString(), player, false));
                data.remove(this.enableMathExpression ? (int) new ExpressionBuilder(result).build().evaluate() : Integer.parseInt(result));
                storageManager.upsertData(player.getUniqueId(), data);
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
