package fr.maxlego08.menu.hooks;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*
* If you have problems compiling this class, delete it there; At the moment, it is impossible to access the nexo API.
* */
public class NexoLoader implements MaterialLoader {

    @Override
    public String getKey() {
        return "nexo";
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        ItemBuilder builder = NexoItems.itemFromId(materialString);
        return builder == null ? null : builder.build();
    }
}
