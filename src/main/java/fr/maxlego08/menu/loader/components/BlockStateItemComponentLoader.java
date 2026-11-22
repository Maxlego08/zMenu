package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BlockStateComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class BlockStateItemComponentLoader extends ItemComponentLoader {

    public BlockStateItemComponentLoader(){
        super("block-state");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> blockStates = componentSection.getStringRouteMappedValues(true);
        if (blockStates.isEmpty()) return null;
        String blockStatesString = blockStates.toString();
        StringBuilder blockStateBuilder = new StringBuilder();
        if (blockStatesString.startsWith("{") && blockStatesString.endsWith("}")) {
            blockStateBuilder.append(blockStatesString, 1, blockStatesString.length() - 1);
        } else {
            blockStateBuilder.append(blockStatesString);
        }
        return new BlockStateComponent(this.getFinalBlockState(blockStateBuilder.toString()));
    }

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @NotNull
    private ResolvableString getFinalBlockState(@NotNull String blockState){
        StringBuilder finalState = new StringBuilder();
        if (!blockState.startsWith("[")){
            finalState.append("[");
        }
        finalState.append(blockState);
        if (!blockState.endsWith("]")) {
            finalState.append("]");
        }
        return ResolvableString.auto(finalState.toString());
    }
}
