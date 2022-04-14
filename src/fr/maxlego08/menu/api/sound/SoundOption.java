package fr.maxlego08.menu.api.sound;

import org.bukkit.entity.Entity;

import fr.maxlego08.menu.api.enums.XSound;

public interface SoundOption {

	/**
	 * 
	 * @return sound
	 */
	public XSound getSound();
	
	/**
	 * 
	 * @return pitch
	 */
	public float getPitch();
	
	/**
	 * 
	 * @return volume
	 */
	public float getVolume();
	
	/**
	 * 
	 * @param entity
	 */
	void play(Entity entity);
	
}
