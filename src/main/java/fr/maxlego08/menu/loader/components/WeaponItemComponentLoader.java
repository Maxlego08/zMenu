package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.WeaponComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Weapon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.5")
public class WeaponItemComponentLoader extends ItemComponentLoader {

    public WeaponItemComponentLoader() {
        super("weapon");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableInt itemDamagePerAttack = this.asResolvableInt(componentSection, "item-damage-per-attack");
        ResolvableFloat disableBlockingForSeconds = this.asResolvableFloat(componentSection, "disable-blocking-for-seconds");

        return new WeaponComponent(
                itemDamagePerAttack != null ? itemDamagePerAttack : ResolvableInt.of(1),
                disableBlockingForSeconds != null ? disableBlockingForSeconds : ResolvableFloat.of(0)
        );
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Weapon weapon = itemStack.getData(DataComponentTypes.WEAPON);
        return weapon == null ? null : new WeaponComponent(ResolvableInt.of(weapon.itemDamagePerAttack()), ResolvableFloat.of(weapon.disableBlockingForSeconds()));
    }
}

