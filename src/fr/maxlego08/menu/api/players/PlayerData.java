package fr.maxlego08.menu.api.players;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PlayerData {

	/**
	 * Return uuid of player
	 * 
	 * @return uniqueId
	 */
	UUID getUniqueId();
	
	/**
	 * Allows to return a data, attention you cannot modify the values directly from this collection.
	 * 
	 * @return collections
	 */
	Collection<Data> getDatas();
	
	/**
	 * Allows you to add a data
	 * 
	 * @param data
	 */
	void addData(Data data);
	
	/**
	 * Allows you to delete a data
	 * 
	 * @param data
	 */
	void removeData(Data data);
	
	/**
	 * Allows you to delete a data
	 * 
	 * @param key
	 */
	void removeData(String key);
	
	/**
	 * Check if data exist
	 * 
	 * @param key
	 * @return boolean
	 */
	boolean containsKey(String key);
	
	/**
	 * Get data
	 * 
	 * @param key 
	 * @return optional
	 */
	Optional<Data> getData(String key);
	
}
