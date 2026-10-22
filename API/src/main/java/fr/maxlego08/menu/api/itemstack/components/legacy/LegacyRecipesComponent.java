package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.RecipesComponent;
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

public class LegacyRecipesComponent extends RecipesComponent {

    public LegacyRecipesComponent(@NotNull List<@Nullable ResolvableNamespacedKey> recipes) {
        super(recipes);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, KnowledgeBookMeta.class, knowledgeBookMeta -> {
            Resolvable.applyResolvable(context, this.getRecipes(), knowledgeBookMeta::setRecipes);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply RecipesComponent to item: " + itemStack.getType().name());
    }
}
