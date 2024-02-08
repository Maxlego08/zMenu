package fr.maxlego08.menu.loader.actions;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BroadcastSoundAction;
import fr.maxlego08.menu.requirement.actions.SoundAction;
import fr.maxlego08.menu.sound.ZSoundOption;

import java.io.File;

public class SoundLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "sound";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {

        String sound = accessor.getString("sound");
        float pitch = accessor.getFloat("pitch", 1f);
        float volume = accessor.getFloat("volume", 1f);
        XSound xSound = sound == null || sound.isEmpty() ? null : XSound.matchXSound(sound).orElse(null);
        return new BroadcastSoundAction(new ZSoundOption(xSound, sound, volume, pitch, xSound == null));
    }
}
