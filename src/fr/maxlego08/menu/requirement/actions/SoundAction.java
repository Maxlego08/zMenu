package fr.maxlego08.menu.requirement.actions;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.bukkit.entity.Player;

public class SoundAction implements Action {

    private final SoundOption soundOption;

    public SoundAction(SoundOption soundOption) {
        this.soundOption = soundOption;
    }

    @Override
    public void execute(Player player) {
        this.soundOption.play(player);
    }
}
