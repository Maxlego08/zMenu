package fr.maxlego08.menu.api.players;

import java.util.Optional;
import java.util.UUID;

import fr.maxlego08.menu.zcore.utils.storage.Saveable;

public interface DataManager extends Saveable{

	/**
	 * 
	 * @param uniqueId
	 * @return
	 */
	Optional<PlayerData> getPlayer(UUID uniqueId);
	
	/**
	 * 
	 * @param uniqueId
	 * @return
	 */
	PlayerData getOrCreate(UUID uniqueId);
	
	/**
	 * 
	 * @param uniqueId
	 * @param data
	 */
	void addData(UUID uniqueId, Data data);
	
	/**
	 * 
	 * @param uniqueId
	 * @param key
	 * @return
	 */
	Optional<Data> getData(UUID uniqueId, String key);

	/**
	 * Clear all player's data
	 */
	void clearAll();
	
	/**
	 * Save auto
	 */
	void autoSave();

	/**
	 * Clear player's data
	 * 
	 * @param uniqueId
	 */
	void clearPlayer(UUID uniqueId);
	
}
