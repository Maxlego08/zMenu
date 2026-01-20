package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class RarityComponent extends ItemComponent {

    private final @Nullable ItemRarity rarity;

    public RarityComponent(@Nullable ItemRarity rarity) {
        this.rarity = rarity;
    }

    public @Nullable ItemRarity getRarity() {
        return this.rarity;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setRarity(this.rarity);
            itemStack.setItemMeta(itemMeta);
        }
    }

}
