package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ItemModelComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import org.bukkit.NamespacedKey;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.3")
public class ItemModelItemComponentLoader extends ItemComponentLoader {

    public ItemModelItemComponentLoader(){
        super("item-model");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        String modelIdStr = configuration.getString(path);
        if (modelIdStr == null) return null;

        if (modelIdStr.contains("%")) {
            return new ItemModelComponent(ResolvableNamespacedKey.of(modelIdStr));
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(modelIdStr);

        return namespacedKey != null ? new ItemModelComponent(ResolvableNamespacedKey.of(namespacedKey)) : null;
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Key model = itemStack.getData(DataComponentTypes.ITEM_MODEL);
        NamespacedKey namespacedKey = model == null ? null : NamespacedKey.fromString(model.asString());
        return namespacedKey == null ? null : new ItemModelComponent(ResolvableNamespacedKey.of(namespacedKey));
    }
}
