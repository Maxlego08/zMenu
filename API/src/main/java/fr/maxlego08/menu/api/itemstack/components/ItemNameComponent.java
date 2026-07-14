package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ItemNameComponent extends ItemComponent {
    private final @NotNull ResolvableString itemName;

    public ItemNameComponent(@NotNull ResolvableString itemName) {
        this.itemName = itemName;
    }

    public @NotNull ResolvableString getItemName() {
        return this.itemName;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            this.applyResolvable(context, itemMeta::setItemName, this.itemName);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
