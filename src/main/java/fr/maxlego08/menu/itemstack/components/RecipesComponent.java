package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record RecipesComponent(
    @NotNull List<@NotNull NamespacedKey> recipes
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, KnowledgeBookMeta.class, knowledgeBookMeta -> {
            knowledgeBookMeta.setRecipes(this.recipes);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply RecipesComponent to item: " + itemStack.getType().name());
    }
}
