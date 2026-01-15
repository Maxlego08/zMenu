package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.ContainerLootComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.loot.LootTables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ContainerLootItemComponentLoader extends ItemComponentLoader {

    public ContainerLootItemComponentLoader(){
        super("container_loot");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        String lootTable = componentSection.getString("loot_table");
        if (lootTable != null) {
            long seed = componentSection.getLong("seed", 0L);
            try {
                LootTables lootTables = LootTables.valueOf(lootTable.toUpperCase());
                return new ContainerLootComponent(lootTables, seed);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}
