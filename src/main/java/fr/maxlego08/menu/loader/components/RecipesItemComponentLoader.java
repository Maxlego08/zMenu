package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.RecipesComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyRecipesComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

@AutoComponentLoader
public class RecipesItemComponentLoader extends ItemComponentLoader {

    public RecipesItemComponentLoader(){
        super("recipes");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);
        List<String> rawRecipes = YamlSections.getStringList(configuration, path);
        List<ResolvableNamespacedKey> recipes = new ArrayList<>();
        for (String rawRecipe : rawRecipes){
            recipes.add(ResolvableNamespacedKey.auto(rawRecipe));
        }
        if (recipes.isEmpty()) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new RecipesComponent(recipes)
                : new LegacyRecipesComponent(recipes);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        List<Key> keys = itemStack.getData(DataComponentTypes.RECIPES);
        if (keys == null || keys.isEmpty()) return null;

        List<ResolvableNamespacedKey> recipes = new ArrayList<>(keys.size());
        for (Key key : keys) {
            NamespacedKey namespacedKey = NamespacedKey.fromString(key.asString());
            if (namespacedKey == null) return null;
            recipes.add(ResolvableNamespacedKey.of(namespacedKey));
        }
        return new RecipesComponent(recipes);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof KnowledgeBookMeta knowledgeBookMeta) || !knowledgeBookMeta.hasRecipes()) return null;
        List<NamespacedKey> keys = knowledgeBookMeta.getRecipes();
        if (keys.isEmpty()) return null;

        List<ResolvableNamespacedKey> recipes = new ArrayList<>(keys.size());
        for (NamespacedKey key : keys) {
            recipes.add(ResolvableNamespacedKey.of(key));
        }
        return new LegacyRecipesComponent(recipes);
    }
}
