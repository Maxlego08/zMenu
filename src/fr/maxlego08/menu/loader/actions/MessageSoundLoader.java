package fr.maxlego08.menu.loader.actions;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.requirement.actions.MessageAction;
import fr.maxlego08.menu.requirement.actions.SoundAction;
import fr.maxlego08.menu.sound.ZSoundOption;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public class MessageSoundLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "sound";
    }

    @Override
    public Action load(String path, Map<String, Object> map, File file) {

        String sound = (String) map.getOrDefault("sound", "");
        float pitch = Float.parseFloat((String) map.getOrDefault("pitch", "1.0f"));
        float volume = Float.parseFloat((String) map.getOrDefault("volume", "1.0f"));
        XSound xSound = sound == null || sound.isEmpty() ? null : XSound.matchXSound(sound).orElse(null);
        return new SoundAction(new ZSoundOption(xSound, pitch, volume));
    }
}
