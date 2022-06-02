package fr.maxlego08.menu.loader.materials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class HeadDatabaseLoader implements MaterialLoader{

	@Override
	public String getKey() {
		return "hdb";
	}

	@Override
	public ItemStack load(YamlConfiguration configuration, String path, String materialString) {
		
		try {

			HeadDatabaseAPI api = new HeadDatabaseAPI();
			return api.getItemHead(materialString);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
