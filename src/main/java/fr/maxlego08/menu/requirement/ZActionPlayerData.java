package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ZActionPlayerData extends ZUtils implements ActionPlayerData {

    private final StorageManager storageManager;
    private final String key;
    private final ActionPlayerDataType type;
    private final Object value;
    private final long seconds;
    private final boolean enableMathExpression;

    public ZActionPlayerData(StorageManager storageManager, String key, ActionPlayerDataType type, Object value, long seconds, boolean enableMathExpression) {
        super();
        this.storageManager = storageManager;
        this.key = key;
        this.type = type;
        this.value = value;
        this.seconds = seconds;
        this.enableMathExpression = enableMathExpression;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public ActionPlayerDataType getType() {
        return this.type;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public long getSeconds() {
        return this.seconds;
    }

    @Override
    public Data toData(OfflinePlayer player) {
        long expiredAt = this.seconds == 0 ? 0 : System.currentTimeMillis() + (1000 * this.seconds);
        String result = papi(this.value.toString(), player, false);
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
    public void execute(Player player, DataManager dataManager) {

        if (this.type == ActionPlayerDataType.REMOVE) {

            Optional<PlayerData> optional = dataManager.getPlayer(player.getUniqueId());
            optional.ifPresent(data -> data.removeData(this.papi(this.key, player, false)));
        } else if (this.type == ActionPlayerDataType.ADD) {

            Optional<Data> optional = dataManager.getData(player.getUniqueId(), this.papi(this.key, player, false));
            if (optional.isPresent()) {
                Data data = optional.get();
                String result = papi(this.value.toString(), player, false);
                data.add(this.enableMathExpression ? (int) new ExpressionBuilder(result).build().evaluate() : Integer.parseInt(result));
                storageManager.upsertData(player.getUniqueId(), data);
            } else {
                dataManager.addData(player.getUniqueId(), this.toData(player));
            }
        } else if (this.type == ActionPlayerDataType.SUBTRACT) {

            Optional<Data> optional = dataManager.getData(player.getUniqueId(), this.papi(this.key, player, false));
            if (optional.isPresent()) {
                Data data = optional.get();
                String result = papi(this.value.toString(), player, false);
                data.remove(this.enableMathExpression ? (int) new ExpressionBuilder(result).build().evaluate() : Integer.parseInt(result));
                storageManager.upsertData(player.getUniqueId(), data);
            } else {
                var data = this.toData(player);
                data.negate();
                dataManager.addData(player.getUniqueId(), data);
            }
        } else if (this.type == ActionPlayerDataType.SET) {
            dataManager.addData(player.getUniqueId(), this.toData(player));
        }
    }
}
