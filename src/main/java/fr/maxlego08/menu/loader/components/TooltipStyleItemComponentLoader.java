package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.TooltipStyleComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.3")
public class TooltipStyleItemComponentLoader extends ItemComponentLoader {

    public TooltipStyleItemComponentLoader(){
        super("tooltip-style");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;
        return new TooltipStyleComponent(ResolvableNamespacedKey.auto(value));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Key style = itemStack.getData(DataComponentTypes.TOOLTIP_STYLE);
        NamespacedKey namespacedKey = style == null ? null : NamespacedKey.fromString(style.asString());
        return namespacedKey == null ? null : new TooltipStyleComponent(ResolvableNamespacedKey.of(namespacedKey));
    }
}
