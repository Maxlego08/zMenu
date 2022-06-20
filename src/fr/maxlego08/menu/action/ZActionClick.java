package fr.maxlego08.menu.action;

import java.util.List;

import fr.maxlego08.menu.api.action.ActiondClick;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;

public class ZActionClick implements ActiondClick {

	private final List<String> messages;
	private final List<String> playerCommands;
	private final List<String> consoleCommands;
	private final OpenLink openLink;
	private final SoundOption soundOption;

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

}
