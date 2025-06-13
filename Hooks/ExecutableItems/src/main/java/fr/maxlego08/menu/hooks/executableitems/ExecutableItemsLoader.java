package fr.maxlego08.menu.hooks.executableitems;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ExecutableItemsLoader extends MaterialLoader {
    public ExecutableItemsLoader() {
        super("ei");
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        return ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(materialString)
                .map(x -> x.buildItem(1, Optional.of(player)))
                .orElse(null);
    }
}
