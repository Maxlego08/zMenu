package fr.maxlego08.menu.zcore.utils;

import java.util.List;

import org.bukkit.entity.Player;

import fr.maxlego08.menu.api.utils.OpenLink;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class ZOpenLink extends ZUtils implements OpenLink {

	private final Action action;
	private final String message;
	private final String link;
	private final String replace;
	private final List<String> hover;

	/**
	 * @param action
	 * @param message
	 * @param link
	 * @param replace
	 * @param hover
	 */
	public ZOpenLink(Action action, String message, String link, String replace, List<String> hover) {
		super();
		this.action = action;
		this.message = message;
		this.link = link;
		this.replace = replace;
		this.hover = hover;
	}

	/**
	 * 
	 */
	public ZOpenLink() {
		this(null, null, null, null, null);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @return the replace
	 */
	public String getReplace() {
		return replace;
	}

	/**
	 * @return the hover
	 */
	public List<String> getHover() {
		return hover;
	}

	@Override
	public Action getAction() {
		return this.action;
	}

	@Override
	public void send(Player player, List<String> messages) {
		if (this.isValid()) {

			messages.forEach(message -> {

				String finalMessage = this.papi(message, player);

				if (finalMessage.contains(this.replace)) {

					String[] splitMessages = finalMessage.split(this.replace);

					TextComponent component = buildTextComponent(splitMessages[0]);

					TextComponent clickComponant = buildTextComponent(color(this.message));
					setClickAction(clickComponant, Action.OPEN_URL, this.link);
					setHoverMessage(clickComponant, color(this.hover));

					component.addExtra(clickComponant);
					if (splitMessages.length == 2) {
						component.addExtra(buildTextComponent(splitMessages[1]));
					}

					player.spigot().sendMessage(component);

				} else {

					player.sendMessage(finalMessage);

				}

			});

		} else {

			messages.forEach(message -> player.sendMessage(this.papi(message, player)));

		}
	}

	@Override
	public boolean isValid() {
		return this.action != null && this.replace != null && this.hover != null && this.link != null
				&& this.message != null;
	}

}
