package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.PiercingWeaponComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
public class PiercingWeaponItemComponentLoader extends ItemComponentLoader {

    public PiercingWeaponItemComponentLoader(){
        super("piercing-weapon");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        ResolvableBoolean dealsKnockback = this.asResolvableBoolean(componentSection, "deals-knockback", true);
        ResolvableBoolean dismounts = this.asResolvableBoolean(componentSection, "dismounts", false);
        ResolvableNamespacedKey sound = ResolvableNamespacedKey.autoOrNull(componentSection.getString("sound"));
        ResolvableNamespacedKey hitSound = ResolvableNamespacedKey.autoOrNull(componentSection.getString("hit-sound"));
        return new PiercingWeaponComponent(dealsKnockback, dismounts, sound, hitSound);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        PiercingWeapon piercingWeapon = itemStack.getData(DataComponentTypes.PIERCING_WEAPON);
        if (piercingWeapon == null) return null;
        return new PiercingWeaponComponent(
                ResolvableBoolean.of(piercingWeapon.dealsKnockback()),
                ResolvableBoolean.of(piercingWeapon.dismounts()),
                this.toResolvableKey(piercingWeapon.sound()),
                this.toResolvableKey(piercingWeapon.hitSound())
        );
    }

    private @Nullable ResolvableNamespacedKey toResolvableKey(@Nullable Key key) {
        if (key == null) return null;
        NamespacedKey namespacedKey = NamespacedKey.fromString(key.asString());
        return namespacedKey == null ? null : ResolvableNamespacedKey.of(namespacedKey);
    }
}
