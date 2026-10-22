package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.MinimumAttackChargeComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
public class MinimumAttackChargeItemComponentLoader extends ItemComponentLoader {

    public MinimumAttackChargeItemComponentLoader(){
        super("minimum-attack-charge");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableFloat charge = this.asResolvableFloat(configuration, path, -1f);
        if (!charge.isDynamic() && (charge.getResolvedValue() == null || charge.getResolvedValue() < 0)) {
            return null;
        }
        return new MinimumAttackChargeComponent(charge);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Float charge = itemStack.getData(DataComponentTypes.MINIMUM_ATTACK_CHARGE);
        return charge == null ? null : new MinimumAttackChargeComponent(ResolvableFloat.of(charge));
    }
}
