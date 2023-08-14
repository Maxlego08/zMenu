package fr.maxlego08.menu.action.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.cryptomorin.xseries.XSound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.action.ZActionClick;
import fr.maxlego08.menu.api.action.ActionClick;
import fr.maxlego08.menu.api.action.data.ActionPlayerData;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.loader.OpenLinkLoader;
import fr.maxlego08.menu.sound.ZSoundOption;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import fr.maxlego08.menu.zcore.utils.loader.Loader;

public class ActionClickLoader implements Loader<ActionClick> {

	private final MenuPlugin plugin;

	/**
	 * @param plugin the plugin
	 */
	public ActionClickLoader(MenuPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	@Override
	public ActionClick load(YamlConfiguration configuration, String path, Object... objects)
			throws InventoryException {

		Loader<ActionPlayerData> loader = new ActionPlayerDataLoader();

		List<String> messages = configuration.getStringList(path + "messages");
		List<String> consoleCommands = configuration.getStringList(path + "consoleCommands");
		List<String> playerCommands = configuration.getStringList(path + "playerCommands");

		SoundOption soundOption = null;
		OpenLink openLink = new ZOpenLink();

		String sound = configuration.getString(path + "sound", null);
		Optional<XSound> optionalXSound = sound == null || sound.isEmpty() ? Optional.empty() : XSound.matchXSound(sound);

		if (optionalXSound.isPresent()) {
			XSound xSound = optionalXSound.get();
			float pitch = Float.parseFloat(configuration.getString(path + "pitch", "1.0f"));
			float volume = Float.parseFloat(configuration.getString(path + "volume", "1.0f"));
			soundOption = new ZSoundOption(xSound, pitch, volume);
		}

		if (configuration.contains(path + "openLink")) {

			Loader<OpenLink> loaderLink = new OpenLinkLoader();
			openLink = loaderLink.load(configuration, path + "openLink.");

		}

		List<ActionPlayerData> actionPlayerDatas = new ArrayList<>();
		if (configuration.isConfigurationSection(path + "datas")) {
			for (String key : configuration.getConfigurationSection(path + "datas.").getKeys(false)) {

				ActionPlayerData actionPlayerData = loader.load(configuration, path + "datas." + key + ".");
				actionPlayerDatas.add(actionPlayerData);

			}
		}

		return new ZActionClick(this.plugin, messages, playerCommands, consoleCommands, openLink, soundOption,
				actionPlayerDatas);
	}

	@Override
	public void save(ActionClick object, YamlConfiguration configuration, String path, File file, Object... objects) {

		configuration.set(path + "messages", object.getMessages());
		configuration.set(path + "consoleCommands", object.getConsoleCommands());
		configuration.set(path + "playerCommands", object.getPlayerCommands());

		if (object.getSound() != null) {
			configuration.set(path + "sound", object.getSound().getSound().toString());
			configuration.set(path + "pitch", object.getSound().getPitch());
			configuration.set(path + "volume", object.getSound().getVolume());
		}

		if (object.getOpenLink() != null) {
			OpenLinkLoader loader = new OpenLinkLoader();
			loader.save(object.getOpenLink(), configuration, path + "openLink.", file);
		}

		if (!object.getPlayerDatas().isEmpty()) {
			ActionPlayerDataLoader loader2 = new ActionPlayerDataLoader();

			for (ActionPlayerData playerData : object.getPlayerDatas()) {
				loader2.save(playerData, configuration, path + "datas." + playerData.getKey() + ".", file);
			}
		}

		try {
			configuration.save(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
