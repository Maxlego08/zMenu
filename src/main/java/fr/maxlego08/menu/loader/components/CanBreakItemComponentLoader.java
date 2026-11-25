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
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.block.BlockType;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

@AutoComponentLoader
@SinceVersion("1.21.3")
public final class CanBreakItemComponentLoader extends ItemComponentLoader {

    public CanBreakItemComponentLoader() {
        super("can-break");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;
        List<ResolvableBlockPredicate> blockPredicateList = new ArrayList<>();
        List<String> blocks = YamlSections.getStringList(componentSection, "blocks");
        for (String block : blocks) {
            TypedKeySetResolvable<BlockType> blockTypes = ResolvableRegistryKeySet.typedKeySetOrNull(RegistryKey.BLOCK, block);
            if (blockTypes != null) {
                blockPredicateList.add(new ResolvableBlockPredicate(blockTypes));
            }
        }
        return blockPredicateList.isEmpty() ? null : new CanBreakComponent(blockPredicateList);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        ItemAdventurePredicate predicate = itemStack.getData(DataComponentTypes.CAN_BREAK);
        if (predicate == null) return null;
        List<ResolvableBlockPredicate> blockPredicates = CanPlaceOnItemComponentLoader.toResolvableBlockPredicates(predicate);
        return blockPredicates == null ? null : new CanBreakComponent(blockPredicates);
    }
}
