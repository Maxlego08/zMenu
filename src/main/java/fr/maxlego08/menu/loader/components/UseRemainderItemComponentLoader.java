package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.UseRemainderComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.21.3")
public class UseRemainderItemComponentLoader extends AbstractMenuItemStackListComponentLoaderBase {

    public UseRemainderItemComponentLoader(MenuPlugin plugin){
        super("use-remainder", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> values = componentSection.getValues(true);
        MenuItemStack menuItemStack = this.loadItemStack(values, file);
        return menuItemStack == null ? null : new UseRemainderComponent(menuItemStack);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        UseRemainder useRemainder = itemStack.getData(DataComponentTypes.USE_REMAINDER);
        if (useRemainder == null) return null;
        MenuItemStack menuItemStack = this.convertItemStack(useRemainder.transformInto());
        return menuItemStack == null ? null : new UseRemainderComponent(menuItemStack);
    }
}
