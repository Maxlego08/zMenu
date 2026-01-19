package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.RecipesComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SpigotRecipesItemComponentLoader extends ItemComponentLoader {

    public SpigotRecipesItemComponentLoader(){
        super("recipes");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        List<String> rawRecipes = configuration.getStringList(path);
        List<NamespacedKey> recipes = new ArrayList<>();
        for (String rawRecipe : rawRecipes){
            NamespacedKey key = NamespacedKey.fromString(rawRecipe);
            if (key != null){
                recipes.add(key);
            }
        }
        return recipes.isEmpty() ? null : new RecipesComponent(recipes);
    }
}
