package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.CanPlaceOnComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableBlockPredicate;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableRegistryKeySet;
import fr.maxlego08.menu.api.utils.resolvable.paper.TypedKeySetResolvable;
import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.tag.Tag;
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
public final class CanPlaceOnItemComponentLoader extends ItemComponentLoader {

    public CanPlaceOnItemComponentLoader() {
        super("can-place-on");
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
        return blockPredicateList.isEmpty() ? null : new CanPlaceOnComponent(blockPredicateList);
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
        ItemAdventurePredicate predicate = itemStack.getData(DataComponentTypes.CAN_PLACE_ON);
        if (predicate == null) return null;
        List<ResolvableBlockPredicate> blockPredicates = toResolvableBlockPredicates(predicate);
        return blockPredicates == null ? null : new CanPlaceOnComponent(blockPredicates);
    }

    static @Nullable List<ResolvableBlockPredicate> toResolvableBlockPredicates(@NotNull ItemAdventurePredicate predicate) {
        if (predicate.predicates().isEmpty()) return null;
        List<ResolvableBlockPredicate> blockPredicates = new ArrayList<>(predicate.predicates().size());
        for (BlockPredicate blockPredicate : predicate.predicates()) {
            RegistryKeySet<BlockType> blocks = blockPredicate.blocks();
            if (blocks == null || blocks instanceof Tag<BlockType> || blocks.values().size() != 1) return null;
            TypedKey<BlockType> block = blocks.values().iterator().next();
            blockPredicates.add(new ResolvableBlockPredicate(ResolvableRegistryKeySet.typedKeySet(RegistryKey.BLOCK, block.key().asString())));
        }
        return blockPredicates;
    }
}
