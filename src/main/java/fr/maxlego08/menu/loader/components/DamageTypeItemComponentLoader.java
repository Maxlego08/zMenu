package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.DamageTypeComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDamageType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
public class DamageTypeItemComponentLoader extends ItemComponentLoader {

    public DamageTypeItemComponentLoader(){
        super("damage-type");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        String damageType = componentSection.getString("types");
        ResolvableDamageType resolvable = ResolvableDamageType.autoOrNull(damageType);
        return resolvable != null ? new DamageTypeComponent(resolvable) : null;
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        DamageType damageType = itemStack.getData(DataComponentTypes.DAMAGE_TYPE);
        if (damageType == null || Registry.DAMAGE_TYPE.getKey(damageType) == null) return null;
        return new DamageTypeComponent(ResolvableDamageType.of(damageType));
    }
}
