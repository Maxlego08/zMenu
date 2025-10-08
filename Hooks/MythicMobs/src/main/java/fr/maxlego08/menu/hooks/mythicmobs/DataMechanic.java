package fr.maxlego08.menu.hooks.mythicmobs;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DataMechanic implements ITargetedEntitySkill {
    private final StorageManager storageManager;
    private final DataManager dataManager;
    private ActionPlayerDataType actionPlayerDataType;
    private final PlaceholderString key;
    private final PlaceholderString value;
    private final long seconds;
    private final boolean enableMathExpression;

    public DataMechanic(MythicLineConfig config, StorageManager storageManager, DataManager dataManager) {
        this.storageManager = storageManager;
        this.dataManager = dataManager;

        this.key = config.getPlaceholderString("key", "");
        this.value = config.getPlaceholderString("value", "");
        this.seconds = config.getLong(new String[]{"seconds"}, 0);
        this.enableMathExpression = config.getBoolean(new String[]{"math"}, false);
        String action = config.getString(new String[]{"action"}, "set").toUpperCase();
        try{
            this.actionPlayerDataType = ActionPlayerDataType.valueOf(action);
        } catch (IllegalArgumentException e){
            Logger.info("Invalid action type: " + action + " for a mechanic in MythicMobs, defaulting to SET");
            this.actionPlayerDataType = ActionPlayerDataType.SET;
        }
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        Entity player = BukkitAdapter.adapt(abstractEntity);

        if (player instanceof Player) {
            switch (this.actionPlayerDataType){
                case REMOVE -> {
                    Optional<PlayerData> optional = dataManager.getPlayer(player.getUniqueId());
                    optional.ifPresent(data -> data.removeData(this.key.get(abstractEntity)));
                }
                case ADD -> {
                    Optional<Data> optional = dataManager.getData(player.getUniqueId(), this.key.get(abstractEntity));
                    if (optional.isPresent()) {
                        Data data = optional.get();
                        String result = this.value.get(abstractEntity);
                        data.add(this.enableMathExpression ? (int) new ExpressionBuilder(result).build().evaluate() : Integer.parseInt(result));
                        storageManager.upsertData(player.getUniqueId(), data);
                    } else {
                        dataManager.addData(player.getUniqueId(), this.toData(abstractEntity));
                    }
                }
                case SUBTRACT -> {
                    Optional<Data> optional = dataManager.getData(player.getUniqueId(), this.key.get(abstractEntity));
                    if (optional.isPresent()) {
                        Data data = optional.get();
                        String result = this.value.get(abstractEntity);
                        data.remove(this.enableMathExpression ? (int) new ExpressionBuilder(result).build().evaluate() : Integer.parseInt(result));
                        storageManager.upsertData(player.getUniqueId(), data);
                    } else {
                        var data = this.toData(abstractEntity);
                        data.negate();
                        dataManager.addData(player.getUniqueId(), data);
                    }
                }
                case SET -> {
                    dataManager.addData(player.getUniqueId(), this.toData(abstractEntity));
                }
            }


            return SkillResult.SUCCESS;
        }
        return SkillResult.ERROR;
    }

    private Data toData(AbstractEntity abstractEntity) {
        long expiredAt = this.seconds == 0 ? 0 : System.currentTimeMillis() + (1000 * this.seconds);
        String result = this.value.get(abstractEntity);
        String dataValue = this.enableMathExpression ? String.valueOf((int) new ExpressionBuilder(result).build().evaluate()) : result;
        return new ZData(this.key.get(abstractEntity), dataValue, expiredAt);
    }
}
