package fr.maxlego08.menu.hooks.executableblocks;

import com.ssomar.score.api.executableblocks.ExecutableBlocksAPI;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class ExecutableBlocksLoader extends MaterialLoader {
    public ExecutableBlocksLoader() {
        super("eb");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        return ExecutableBlocksAPI.getExecutableBlocksManager().getExecutableBlock(materialString)
                .map(x -> x.buildItem(1, Optional.of(player)))
                .orElse(null);
    }
}
