package fr.maxlego08.menu.hooks;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

/*
* If you have problems compiling this class, delete it there; At the moment, it is impossible to access the nexo API.
* */
public class NexoLoader extends MaterialLoader {

    public NexoLoader() {
        super("nexo");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        ItemBuilder builder = NexoItems.itemFromId(materialString);
        return builder == null ? null : builder.build();
    }
}
