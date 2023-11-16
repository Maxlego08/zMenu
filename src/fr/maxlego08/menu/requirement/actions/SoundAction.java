package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

public class SoundAction implements Action {

    private final SoundOption soundOption;

    public SoundAction(SoundOption soundOption) {
        this.soundOption = soundOption;
    }

    @Override
    public void execute(Player player, Button button, InventoryDefault inventory) {
        this.soundOption.play(player);
    }
}
