package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.RepairCostComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyRepairCostComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
public class RepairCostItemComponentLoader extends ItemComponentLoader {

    public RepairCostItemComponentLoader(){
        super("repair-cost");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableInt cost = this.asResolvableInt(configuration, path);
        if (cost == null) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new RepairCostComponent(cost)
                : new LegacyRepairCostComponent(cost);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Integer cost = itemStack.getData(DataComponentTypes.REPAIR_COST);
        return cost == null ? null : new RepairCostComponent(ResolvableInt.of(cost));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof Repairable repairable) || !repairable.hasRepairCost()) return null;
        return new LegacyRepairCostComponent(ResolvableInt.of(repairable.getRepairCost()));
    }
}
