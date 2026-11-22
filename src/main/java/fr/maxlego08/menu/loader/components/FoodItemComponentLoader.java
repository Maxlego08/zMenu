package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.FoodComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyFoodComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class FoodItemComponentLoader extends ItemComponentLoader {

    public FoodItemComponentLoader() {
        super("food");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;

        ResolvableInt nutrition = this.asResolvableInt(componentSection, "nutrition");
        ResolvableFloat saturation = this.asResolvableFloat(componentSection, "saturation");
        ResolvableBoolean canAlwaysEat = this.asResolvableBoolean(componentSection, "can-always-eat");

        if (nutrition == null || saturation == null) {
            return null;
        }

        canAlwaysEat = canAlwaysEat != null ? canAlwaysEat : ResolvableBoolean.of(false);
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new FoodComponent(nutrition, saturation, canAlwaysEat)
                : new LegacyFoodComponent(nutrition, saturation, canAlwaysEat);
    }

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        FoodProperties food = itemStack.getData(DataComponentTypes.FOOD);
        if (food == null) return null;
        return new FoodComponent(ResolvableInt.of(food.nutrition()), ResolvableFloat.of(food.saturation()), ResolvableBoolean.of(food.canAlwaysEat()));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasFood()) return null;
        org.bukkit.inventory.meta.components.FoodComponent food = itemMeta.getFood();
        return new LegacyFoodComponent(ResolvableInt.of(food.getNutrition()), ResolvableFloat.of(food.getSaturation()), ResolvableBoolean.of(food.canAlwaysEat()));
    }
}
