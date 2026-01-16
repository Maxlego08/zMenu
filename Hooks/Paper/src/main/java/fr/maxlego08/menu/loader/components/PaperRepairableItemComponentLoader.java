package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.paper.components.PaperRepairableComponent;
import io.papermc.paper.datacomponent.item.Repairable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PaperRepairableItemComponentLoader extends ItemComponentLoader {

    public PaperRepairableItemComponentLoader(){
        super("repairable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        String string = componentSection.getString("items");
        List<String> items = componentSection.getStringList("items");
        List<TypedKey<ItemType>> itemKeys = new ArrayList<>();
        for (String item : items) {
            itemKeys.add(TypedKey.create(RegistryKey.ITEM, Key.key(item)));
        }
        if (string != null){
            itemKeys.add(TypedKey.create(RegistryKey.ITEM, Key.key(string)));
        }
        return itemKeys.isEmpty() ? null : new PaperRepairableComponent(Repairable.repairable(RegistrySet.keySet(RegistryKey.ITEM, itemKeys)));
    }
}
