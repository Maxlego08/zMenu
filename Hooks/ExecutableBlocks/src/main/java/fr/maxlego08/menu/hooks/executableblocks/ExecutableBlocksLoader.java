package fr.maxlego08.menu.hooks.executableblocks;

import com.ssomar.score.api.executableblocks.ExecutableBlocksAPI;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ExecutableBlocksLoader extends MaterialLoader {
    public ExecutableBlocksLoader() {
        super("eb");
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        return ExecutableBlocksAPI.getExecutableBlocksManager().getExecutableBlock(materialString)
                .map(x -> x.buildItem(1, Optional.of(player)))
                .orElse(null);
    }
}
