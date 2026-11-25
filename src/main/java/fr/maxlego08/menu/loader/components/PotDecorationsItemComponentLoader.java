package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.PotDecorationsComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyPotDecorationsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotDecorations;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.block.DecoratedPot;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

@AutoComponentLoader
public class PotDecorationsItemComponentLoader extends ItemComponentLoader {
    private static final int SIDES = 4;
    private static final DecoratedPot.Side[] LEGACY_ORDER = {DecoratedPot.Side.BACK, DecoratedPot.Side.LEFT, DecoratedPot.Side.RIGHT, DecoratedPot.Side.FRONT};

    public PotDecorationsItemComponentLoader() {
        super("pot-decorations");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);
        List<String> decorations = YamlSections.getStringList(configuration, path);

        if (decorations.size() < SIDES) return null;

        ResolvableRegistryEntry<ItemType>[] keys = new ResolvableRegistryEntry[SIDES];
        for (int i = 0; i < SIDES; i++) {
            keys[i] = ResolvableRegistry.auto(decorations.get(i), RegistryKey.ITEM);
        }

        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new PotDecorationsComponent(keys)
                : new LegacyPotDecorationsComponent(keys);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        PotDecorations potDecorations = itemStack.getData(DataComponentTypes.POT_DECORATIONS);
        if (potDecorations == null) return null;

        ItemType[] types = {potDecorations.back(), potDecorations.left(), potDecorations.right(), potDecorations.front()};
        ResolvableRegistryEntry<ItemType>[] sides = new ResolvableRegistryEntry[SIDES];
        for (int i = 0; i < SIDES; i++) {
            if (types[i] == null) return null;
            sides[i] = ResolvableRegistry.ofRegisteredOrNull(types[i], RegistryKey.ITEM);
            if (sides[i] == null) return null;
        }
        return new PotDecorationsComponent(sides);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta)) return null;
        if (!(blockStateMeta.getBlockState() instanceof DecoratedPot decoratedPot)) return null;

        Material[] materials = new Material[SIDES];
        boolean decorated = false;
        for (int i = 0; i < SIDES; i++) {
            Material sherd = decoratedPot.getSherd(LEGACY_ORDER[i]);
            materials[i] = sherd == null ? Material.BRICK : sherd;
            if (materials[i] != Material.BRICK) decorated = true;
        }
        if (!decorated) return null;

        ResolvableRegistryEntry<ItemType>[] sides = new ResolvableRegistryEntry[SIDES];
        for (int i = 0; i < SIDES; i++) {
            ItemType itemType = materials[i].asItemType();
            if (itemType == null) return null;
            sides[i] = ResolvableRegistry.ofRegisteredOrNull(itemType, RegistryKey.ITEM);
            if (sides[i] == null) return null;
        }
        return new LegacyPotDecorationsComponent(sides);
    }
}
