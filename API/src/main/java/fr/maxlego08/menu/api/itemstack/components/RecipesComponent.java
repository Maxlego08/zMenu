package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class RecipesComponent extends ItemComponent {
    private final @NotNull List<@Nullable ResolvableNamespacedKey> recipes;

    public RecipesComponent(@NotNull List<@Nullable ResolvableNamespacedKey> recipes) {
        this.recipes = recipes;
    }

    public @NotNull List<@Nullable ResolvableNamespacedKey> getRecipes() {
        return this.recipes;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, KnowledgeBookMeta.class, knowledgeBookMeta -> {

            Resolvable.applyResolvable(context, this.recipes, knowledgeBookMeta::setRecipes);

        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply RecipesComponent to item: " + itemStack.getType().name());
    }
}
