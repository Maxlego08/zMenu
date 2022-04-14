package fr.maxlego08.menu.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuEvent extends Event {

	private final static HandlerList handlers = new HandlerList();

	 
	
	/**
	 * 
	 */
	public MenuEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param isAsync
	 */
	public MenuEvent(boolean isAsync) {
		super(isAsync);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the handlers
	 */
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public void callEvent(){
		Bukkit.getPluginManager().callEvent(this);
	}

}
