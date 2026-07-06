package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.CanBreakComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableBlockPredicate;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableRegistryKeySet;
import fr.maxlego08.menu.api.utils.resolvable.paper.TypedKeySetResolvable;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.20.5")
public final class CanBreakItemComponentLoader extends ItemComponentLoader {

    public CanBreakItemComponentLoader() {
        super("can-break");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        List<ResolvableBlockPredicate> blockPredicateList = new ArrayList<>();
        List<String> blocks = componentSection.getStringList("blocks");
        for (String block : blocks) {
            TypedKeySetResolvable<BlockType> blockTypes = ResolvableRegistryKeySet.typedKeySetOrNull(RegistryKey.BLOCK, block);
            if (blockTypes != null) {
                blockPredicateList.add(new ResolvableBlockPredicate(blockTypes));
            }
        }
        return blockPredicateList.isEmpty() ? null : new CanBreakComponent(blockPredicateList);
    }
}
