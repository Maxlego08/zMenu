package fr.maxlego08.menu.api.action.data;

import fr.maxlego08.menu.api.players.Data;

public interface ActionPlayerData {

	/**
	 * The key, be careful to have only unique keys
	 * 
	 * @return key
	 */
	String getKey();
	
	/**
	 * Action type
	 * 
	 * @return type
	 */
	ActionPlayerDataType getType();
	
	/**
	 * The value that should be used
	 * 
	 * @return value
	 */
	Object getValue();
	
	/**
	 * The number of seconds to expire the data, put 0 to have no expiration
	 * 
	 * @return seconds 
	 */
	long getSeconds();

	/**
	 * Create new data
	 * 
	 * @return data
	 */
	Data toData();
	
}
