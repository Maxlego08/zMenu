package fr.maxlego08.menu.action;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.action.ActiondClick;
import fr.maxlego08.menu.api.action.data.ActionPlayerData;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public class ZActionClick extends ZUtils implements ActiondClick {

	private final MenuPlugin plugin;
	private final List<String> messages;
	private final List<String> playerCommands;
	private final List<String> consoleCommands;
	private final SoundOption soundOption;
	private final List<ActionPlayerData> datas;
	private OpenLink openLink = new ZOpenLink();

	/**
	 * @param messages
	 * @param playerCommands
	 * @param consoleCommands
	 * @param soundOption
	 * @param datas
	 * @param openLink
	 */
	public ZActionClick(MenuPlugin plugin, List<String> messages, List<String> playerCommands,
			List<String> consoleCommands, OpenLink openLink, SoundOption soundOption, List<ActionPlayerData> datas) {
		super();
		this.messages = messages;
		this.playerCommands = playerCommands;
		this.consoleCommands = consoleCommands;
		this.soundOption = soundOption;
		this.datas = datas;
		this.openLink = openLink;
		this.plugin = plugin;
	}

	@Override
	public List<String> getMessages() {
		return this.messages;
	}

	@Override
	public List<String> getPlayerCommands() {
		return this.playerCommands;
	}

	@Override
	public List<String> getConsoleCommands() {
		return this.consoleCommands;
	}

	@Override
	public OpenLink getOpenLink() {
		return this.openLink;
	}

	@Override
	public SoundOption getSound() {
		return this.soundOption;
	}

	@Override
	public void execute(Player player) {

		if (!this.datas.isEmpty()) {

			DataManager dataManager = this.plugin.getDataManager();
			for (ActionPlayerData actionPlayerData : this.datas) {
				actionPlayerData.execute(player, dataManager);
			}

		}
		
		Plugin plugin = Bukkit.getPluginManager().getPlugin("zMenu");
		Bukkit.getScheduler().runTask(plugin, () -> {

			this.consoleCommands.forEach(command -> {
				String commandLine = papi(command.replace("%player%", player.getName()), player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
			});

			this.playerCommands.forEach(command -> {
				String commandLine = papi(command.replace("%player%", player.getName()), player);
				Bukkit.dispatchCommand(player, commandLine);
			});

		});

		this.openLink.send(player, this.messages);

		if (this.soundOption != null) {
			this.soundOption.play(player);
		}	

	}

	@Override
	public List<ActionPlayerData> getPlayerDatas() {
		return this.datas;
	}

}
