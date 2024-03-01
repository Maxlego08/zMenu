package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastSoundAction extends Action {

    private final SoundOption soundOption;

    public BroadcastSoundAction(SoundOption soundOption) {
        this.soundOption = soundOption;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        Bukkit.getOnlinePlayers().forEach(this.soundOption::play);
    }
}
