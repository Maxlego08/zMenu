package fr.maxlego08.menu.button;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.SwitchButton;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.entity.Player;

import java.util.Map;

public class ZSwitchButton extends ZUtils implements SwitchButton {

    private final String key;

    private final Map<String, Button> switchButton;

    public ZSwitchButton(String key, Map<String, Button> switchButton) {
        this.key = key;
        this.switchButton = switchButton;
    }

    @Override
    public Map<String, Button> getSwitchButton() {
        return switchButton;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Button perform(Player player){
        return this.switchButton.getOrDefault(papi(this.key, player, true), this.switchButton.get("default"));
    }
}
