package fr.maxlego08.menu.api.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.button.Button;

public interface ButtonLoader {

	/**
	 * Returns the class that will be used for the button
	 * 
	 * @return classz
	 */
	public Class<? extends Button> getButton();

	/**
	 * Returns the name of the button
	 * 
	 * @return name
	 */
	public String getName();
	
	/**
	 * Return the plugin where the loader button comes from
	 * 
	 * @return plugin
	 */
	public Plugin getPlugin();
	
	/**
	 * Allows you to load a button
	 * 
	 * @param buttonName
	 * @param itemStack
	 * @param slot
	 * @param isPermanent
	 * @param permission
	 * @param elseButton
	 * @param action
	 * @param placeholder
	 * @param value
	 */
	public Button load(YamlConfiguration configuration, String path);
	
	/**
	 * Allows to load a list of string to transform it into integer
	 * Allows to have several formats for the slots
	 * More informations here: https://zmenu.groupez.dev/configurations/buttons#none_slot
	 * 
	 * @param slotsAsString
	 * @return slots
	 */
	static List<Integer> loadSlot(List<String> slotsAsString){
		List<Integer> slots = new ArrayList<Integer>();
		if (slotsAsString.size() > 0) {
			for (String line : slotsAsString) {
				if (line.contains("-")) {
					try {
						String[] values = line.split("-");
						int from = Integer.valueOf(values[0]);
						int to = Integer.valueOf(values[1]) + 1;
						slots.addAll(IntStream.range(Math.min(from, to), Math.max(from, to)).boxed()
								.collect(Collectors.toList()));
					} catch (Exception ignored) {
						ignored.printStackTrace();
					}
				} else {
					try {
						slots.add(Integer.parseInt(line));
					} catch (NumberFormatException e) {
					}
				}
			}
		}
		return slots;
	}
	
}
