package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.RepairableComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Repairable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.Tag;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.21.3")
public class RepairableItemComponentLoader extends ItemComponentLoader {

    public RepairableItemComponentLoader(){
        super("repairable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        List<String> items = componentSection.isList("items") ? componentSection.getStringList("items") : new ArrayList<>();
        String string = componentSection.isList("items") ? null : componentSection.getString("items");
        List<TypedKey<ItemType>> itemKeys = new ArrayList<>();
        for (String item : items) {
            itemKeys.add(TypedKey.create(RegistryKey.ITEM, Key.key(item)));
        }
        if (string != null){
            itemKeys.add(TypedKey.create(RegistryKey.ITEM, Key.key(string)));
        }
        return itemKeys.isEmpty() ? null : new RepairableComponent(Repairable.repairable(RegistrySet.keySet(RegistryKey.ITEM, itemKeys)));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Repairable repairable = itemStack.getData(DataComponentTypes.REPAIRABLE);
        if (repairable == null || repairable.types() instanceof Tag<ItemType> || repairable.types().values().isEmpty()) return null;
        return new RepairableComponent(Repairable.repairable(RegistrySet.keySet(RegistryKey.ITEM, new ArrayList<>(repairable.types().values()))));
    }
}
