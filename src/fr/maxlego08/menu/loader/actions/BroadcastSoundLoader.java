package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BroadcastSoundAction;

import java.io.File;

public class BroadcastSoundLoader extends SoundLoader {

    @Override
    public String getKey() {
        return "broadcast_sound,broadcast sound";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        return new BroadcastSoundAction(loadSound(path, accessor, file));
    }
}
