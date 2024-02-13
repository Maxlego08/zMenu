package fr.maxlego08.menu.loader.actions;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BroadcastSoundAction;
import fr.maxlego08.menu.sound.ZSoundOption;

import java.io.File;

public class BroadcastSoundLoader extends SoundLoader {

    @Override
    public String getKey() {
        return "broadcast_sound";
    }
}
