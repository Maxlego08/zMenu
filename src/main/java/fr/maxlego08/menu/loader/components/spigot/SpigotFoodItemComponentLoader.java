package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.FoodComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotFoodItemComponentLoader extends ItemComponentLoader {

    public SpigotFoodItemComponentLoader(){
        super("food");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        int nutrition = componentSection.getInt("nutrition", -1);
        float saturation = (float) componentSection.getDouble("saturation", -1);
        boolean canAlwaysEat = componentSection.getBoolean("can_always_eat", false);
        if (nutrition < 0 || saturation < 0) {
            return null;
        }
        return new FoodComponent(nutrition, saturation, canAlwaysEat);
    }
}
