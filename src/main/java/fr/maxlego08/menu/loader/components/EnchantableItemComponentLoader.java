package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.EnchantableComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Enchantable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.3")
public class EnchantableItemComponentLoader extends ItemComponentLoader {

    public EnchantableItemComponentLoader(){
        super("enchantable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        ResolvableInt value = this.asResolvableInt(componentSection, "value");
        if (value == null) return null;
        if (!value.isDynamic()) {
            Integer resolved = value.getResolvedValue();
            if (resolved == null || resolved < 0) return null;
        }
        return new EnchantableComponent(value);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Enchantable enchantable = itemStack.getData(DataComponentTypes.ENCHANTABLE);
        return enchantable == null ? null : new EnchantableComponent(ResolvableInt.of(enchantable.value()));
    }
}
