package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.placeholder.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ItemModelComponent extends ItemComponent {
    private final @NotNull String itemModel;

    public ItemModelComponent(@NotNull String itemModel) {
        this.itemModel = itemModel;
    }

    public @NotNull String getItemModel() {
        return this.itemModel;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            String s = Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, context.getPlaceholders().parse(this.itemModel));
            NamespacedKey finalItemModel = null;
            try {
                String[] split = s.split(":", 2);
                if (split.length == 2) {
                    finalItemModel = new NamespacedKey(split[0], split[1]);
                } else {
                    finalItemModel = NamespacedKey.minecraft(s);
                }
            } catch (Exception _) {
            }

            if (finalItemModel != null) {
                itemMeta.setItemModel(finalItemModel);
            }

            itemStack.setItemMeta(itemMeta);
        }
    }
}
