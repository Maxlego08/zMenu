package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.players.ZData;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ZActionPlayerData implements ActionPlayerData {

    private final String key;
    private final ActionPlayerDataType type;
    private final Object value;
    private final long seconds;

    /**
     * @param key
     * @param type
     * @param value
     * @param seconds
     */
    public ZActionPlayerData(String key, ActionPlayerDataType type, Object value, long seconds) {
        super();
        this.key = key;
        this.type = type;
        this.value = value;
        this.seconds = seconds;
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
    public Data toData() {
        long expiredAt = this.seconds == 0 ? 0 : System.currentTimeMillis() + (1000 * this.seconds);
        return new ZData(this.key, this.value, expiredAt);
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
            if (optional.isPresent()) {
                PlayerData data = optional.get();
                data.removeData(this.key);
            }
        } else if (this.type == ActionPlayerDataType.ADD) {

            Optional<Data> optional = dataManager.getData(player.getUniqueId(), this.key);
            if (optional.isPresent()) {
                Data data = optional.get();
                data.add(Integer.parseInt(this.value.toString()));
            } else {
                dataManager.addData(player.getUniqueId(), this.toData());
            }
        } else if (this.type == ActionPlayerDataType.SUBTRACT) {

            Optional<Data> optional = dataManager.getData(player.getUniqueId(), this.key);
            if (optional.isPresent()) {
                Data data = optional.get();
                data.remove(Integer.parseInt(this.value.toString()));
            } else {
                long expiredAt = this.seconds == 0 ? 0 : System.currentTimeMillis() + (1000 * this.seconds);
                dataManager.addData(player.getUniqueId(), new ZData(this.key, -(int) this.value, expiredAt));
            }
        } else if (this.type == ActionPlayerDataType.SET) {
            dataManager.addData(player.getUniqueId(), this.toData());
        }

        dataManager.autoSave();
    }
}
