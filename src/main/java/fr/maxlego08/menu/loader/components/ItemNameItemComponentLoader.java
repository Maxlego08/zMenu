package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ItemNameComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyItemNameComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
public class ItemNameItemComponentLoader extends ItemComponentLoader {

    public ItemNameItemComponentLoader(){
        super("item-name");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        String name = configuration.getString(path);
        if (name == null) {
            return null;
        }
        ResolvableString itemName = ResolvableString.autoOrNull(name);
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new ItemNameComponent(itemName)
                : new LegacyItemNameComponent(itemName);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Component itemName = itemStack.getData(DataComponentTypes.ITEM_NAME);
        String text = itemName == null ? null : toLegacyText(itemName);
        return text == null ? null : new ItemNameComponent(ResolvableString.of(text));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        Component itemName = itemMeta == null || !itemMeta.hasItemName() ? null : itemMeta.itemName();
        String text = itemName == null ? null : toLegacyText(itemName);
        return text == null ? null : new LegacyItemNameComponent(ResolvableString.of(text));
    }
}
