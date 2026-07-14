package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ContainerLootComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableLong;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.loot.LootTables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotContainerLootItemComponentLoader extends ItemComponentLoader {

    public SpigotContainerLootItemComponentLoader(){
        super("container-loot");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
//         String lootTableStr = componentSection.getString("loot-table");
//         if (lootTableStr == null) return null;
//
//         Resolvable<String> lootTable = lootTableStr.contains("%") ? ResolvableString.ofExpression(lootTableStr) : ResolvableString.of(lootTableStr);
        ResolvableEnum<LootTables> lootTablesResolvable = ResolvableEnum.autoOrNull(LootTables.class, componentSection.getString("loot-table"));
        ResolvableLong seed = this.asResolvableLong(componentSection, "seed", 0L);
        return new ContainerLootComponent(lootTablesResolvable, seed);
    }
}
