package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ItemModelComponent extends ItemComponent {
    private final @NotNull ResolvableNamespacedKey itemModel;

    public ItemModelComponent(@NotNull String itemModel) {
        this.itemModel = ResolvableNamespacedKey.of(itemModel);
    }

    public ItemModelComponent(@NotNull ResolvableNamespacedKey itemModel) {
        this.itemModel = itemModel;
    }

    public @NotNull ResolvableNamespacedKey getItemModel() {
        return this.itemModel;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        this.applyResolvable(context, itemModel -> itemStack.setData(DataComponentTypes.ITEM_MODEL, itemModel), this.itemModel);
    }

    @Override
    public @Nullable Object serialize() {
        return this.itemModel.serialize();
    }
}
