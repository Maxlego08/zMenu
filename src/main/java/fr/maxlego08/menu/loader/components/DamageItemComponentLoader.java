package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.DamageComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyDamageComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
public class DamageItemComponentLoader extends ItemComponentLoader {

    public DamageItemComponentLoader(){
        super("damage");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableInt damages = this.asResolvableInt(configuration, path);
        if (damages != null) {
            return MinecraftVersion.isServerAtLeast("1.21.3")
                    ? new DamageComponent(damages)
                    : new LegacyDamageComponent(damages);
        }
        return null;
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Integer damage = itemStack.getData(DataComponentTypes.DAMAGE);
        return damage == null ? null : new DamageComponent(ResolvableInt.of(damage));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof Damageable damageable) || !damageable.hasDamage()) return null;
        return new LegacyDamageComponent(ResolvableInt.of(damageable.getDamage()));
    }
}
