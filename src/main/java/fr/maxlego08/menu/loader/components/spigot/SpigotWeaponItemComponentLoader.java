package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.WeaponComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.2")
public class SpigotWeaponItemComponentLoader extends ItemComponentLoader {

    public SpigotWeaponItemComponentLoader() {
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
}

