package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.VillagerFoodComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.VillagerFood;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("26.3")
public final class VillagerFoodItemComponentLoader extends ItemComponentLoader {

    public VillagerFoodItemComponentLoader() {
        super("villager-food");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        ResolvableInt nutrition = this.asResolvableInt(componentSection, "nutrition");
        return nutrition == null ? null : new VillagerFoodComponent(nutrition);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        VillagerFood villagerFood = itemStack.getData(DataComponentTypes.VILLAGER_FOOD);
        return villagerFood == null ? null : new VillagerFoodComponent(ResolvableInt.of(villagerFood.nutrition()));
    }
}
