package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ContainerLootComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyContainerLootComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableLong;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SeededContainerLoot;
import org.bukkit.NamespacedKey;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class ContainerLootItemComponentLoader extends ItemComponentLoader {

    public ContainerLootItemComponentLoader(){
        super("container-loot");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;ResolvableEnum<LootTables> lootTablesResolvable = ResolvableEnum.autoOrNull(LootTables.class, componentSection.getString("loot-table"));
        ResolvableLong seed = this.asResolvableLong(componentSection, "seed", 0L);
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new ContainerLootComponent(lootTablesResolvable, seed)
                : new LegacyContainerLootComponent(lootTablesResolvable, seed);
    }

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        SeededContainerLoot containerLoot = itemStack.getData(DataComponentTypes.CONTAINER_LOOT);
        if (containerLoot == null) return null;
        String lootTableKey = containerLoot.lootTable().asString();
        for (LootTables lootTable : LootTables.values()) {
            if (lootTable.getKey().asString().equals(lootTableKey)) {
                return new ContainerLootComponent(ResolvableEnum.of(LootTables.class, lootTable), ResolvableLong.of(containerLoot.seed()));
            }
        }
        return null;
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof BlockStateMeta blockStateMeta)) return null;
        if (!(blockStateMeta.getBlockState() instanceof Lootable lootable)) return null;
        LootTable currentLootTable = lootable.getLootTable();
        if (currentLootTable == null) return null;

        NamespacedKey lootTableKey = currentLootTable.getKey();
        for (LootTables lootTable : LootTables.values()) {
            if (lootTable.getKey().equals(lootTableKey)) {
                return new LegacyContainerLootComponent(ResolvableEnum.of(LootTables.class, lootTable), ResolvableLong.of(lootable.getSeed()));
            }
        }
        return null;
    }
}
