package fr.maxlego08.menu.action;

import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.maxlego08.menu.api.action.ActiondClick;
import fr.maxlego08.menu.api.enums.XSound;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.loader.OpenLinkLoader;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import fr.maxlego08.menu.zcore.utils.loader.Loader;

public class ActionClickLoader implements Loader<ActiondClick> {

	@Override
	public ActiondClick load(YamlConfiguration configuration, String path, Object... objects)
			throws InventoryException {

		List<String> messages = configuration.getStringList(path + "messages");
		List<String> consoleCommands = configuration.getStringList(path + "consoleCommands");
		List<String> playerCommands = configuration.getStringList(path + "playerCommands");

		SoundOption soundOption = null;
		OpenLink openLink = new ZOpenLink();

		Optional<XSound> optionalXSound = XSound.matchXSound(configuration.getString(path + "sound", null));

		if (optionalXSound.isPresent()) {
			XSound xSound = optionalXSound.get();
			float pitch = Float.valueOf(configuration.getString(path + "pitch", "1.0f"));
			float volume = Float.valueOf(configuration.getString(path + "volume", "1.0f"));
			soundOption = new ZSoundOption(xSound, pitch, volume);
		}

		if (configuration.contains(path + "openLink")) {

			Loader<OpenLink> loaderLink = new OpenLinkLoader();
			openLink = loaderLink.load(configuration, path + "openLink.");

		}

		return new ZActionClick(messages, playerCommands, consoleCommands, openLink, soundOption);
	}

	@Override
	public void save(ActiondClick object, YamlConfiguration configuration, String path, Object... objects) {
		// TODO Auto-generated method stub

	}

}
