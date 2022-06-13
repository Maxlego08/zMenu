package fr.maxlego08.menu.zcore.utils;

import java.util.List;

import fr.maxlego08.menu.api.utils.OpenLink;

public class ZOpenLink implements OpenLink {

	private final String message;
	private final String link;
	private final String replace;
	private final List<String> hover;

	/**
	 * @param message
	 * @param link
	 * @param replace
	 * @param hover
	 */
	public ZOpenLink(String message, String link, String replace, List<String> hover) {
		super();
		this.message = message;
		this.link = link;
		this.replace = replace;
		this.hover = hover;
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

}
