package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastSoundAction implements Action {

    private final SoundOption soundOption;

    public BroadcastSoundAction(SoundOption soundOption) {
        this.soundOption = soundOption;
    }

    @Override
    public void execute(Player ignored) {
        Bukkit.getOnlinePlayers().forEach(this.soundOption::play);
    }
}
