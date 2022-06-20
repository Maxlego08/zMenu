package fr.maxlego08.menu.action;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.action.ActiondClick;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public class ZActionClick extends ZUtils implements ActiondClick {

	private final List<String> messages;
	private final List<String> playerCommands;
	private final List<String> consoleCommands;
	private final SoundOption soundOption;
	private OpenLink openLink = new ZOpenLink();

	/**
	 * @param messages
	 * @param playerCommands
	 * @param consoleCommands
	 * @param openLink
	 * @param soundOption
	 */
	public ZActionClick(List<String> messages, List<String> playerCommands, List<String> consoleCommands,
			OpenLink openLink, SoundOption soundOption) {
		super();
		this.messages = messages;
		this.playerCommands = playerCommands;
		this.consoleCommands = consoleCommands;
		this.openLink = openLink;
		this.soundOption = soundOption;
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

		Plugin plugin = Bukkit.getPluginManager().getPlugin("zMenu");
		Bukkit.getScheduler().runTask(plugin, () -> {

			this.consoleCommands.forEach(command -> {
				String commandLine = papi(command, player);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
			});
			
			this.playerCommands.forEach(command -> {
				String commandLine = papi(command, player);
				Bukkit.dispatchCommand(player, commandLine);
			});
			
		});

		this.openLink.send(player, this.messages);

		if (this.soundOption != null) {
			this.soundOption.play(player);
		}

	}

}
