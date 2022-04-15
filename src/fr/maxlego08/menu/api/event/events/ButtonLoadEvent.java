package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.event.MenuEvent;
import fr.maxlego08.menu.api.loader.ButtonLoader;

public class ButtonLoadEvent extends MenuEvent {

	private final ButtonManager buttonManager;

	/**
	 * @param buttonManager
	 */
	public ButtonLoadEvent(ButtonManager buttonManager) {
		super();
		this.buttonManager = buttonManager;
	}

	/**
	 * @return the buttonManager
	 */
	public ButtonManager getButtonManager() {
		return this.buttonManager;
	}

	/**
	 * Register a ButtonLoader
	 * 
	 * @param button
	 */
	public void register(ButtonLoader button) {
		this.buttonManager.register(button);
	}

}
