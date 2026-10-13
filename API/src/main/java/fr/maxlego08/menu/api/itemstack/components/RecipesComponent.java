package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    public @Nullable Object serialize() {
        return Resolvable.serializeList(this.recipes);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.recipes, recipes -> itemStack.setData(DataComponentTypes.RECIPES, new ArrayList<Key>(recipes)));
    }
}
