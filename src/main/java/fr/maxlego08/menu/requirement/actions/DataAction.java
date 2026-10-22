package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class DataAction extends Action {

    private final ActionPlayerData playerData;
    private final MenuPlugin plugin;

    public DataAction(ActionPlayerData playerData, MenuPlugin plugin) {
        this.playerData = playerData;
        this.plugin = plugin;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        this.playerData.execute(player, this.plugin.getDataManager(),placeholders);
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "data");
        if (this.playerData.getType() != ActionPlayerDataType.SET) map.put("action", this.playerData.getType().name());
        map.put("key", this.playerData.getKey());
        Object value = this.playerData.getValue();
        if (!Boolean.TRUE.equals(value)) map.put("value", value);
        String seconds = this.playerData.getSeconds();
        if (!"null".equals(seconds)) map.put("seconds", seconds);
        if (this.playerData.isMathExpression()) map.put("math", true);
    }
}
