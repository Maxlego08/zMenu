package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.BlockStateComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

public class BlockStateItemComponentLoader extends ItemComponentLoader {

    public BlockStateItemComponentLoader(){
        super("block_state");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> blockStates = componentSection.getValues(true);
        if (blockStates.isEmpty()) return null;
        String blockStatesString = blockStates.toString();
        StringBuilder blockStateBuilder = new StringBuilder();
        if (blockStatesString.startsWith("{") && blockStatesString.endsWith("}")) {
            blockStateBuilder.append(blockStatesString, 1, blockStatesString.length() - 1);
        } else {
            blockStateBuilder.append(blockStatesString);
        }
        return new BlockStateComponent(blockStateBuilder.toString());
    }
}
