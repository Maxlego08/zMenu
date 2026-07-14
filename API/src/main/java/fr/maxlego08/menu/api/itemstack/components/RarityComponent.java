package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class RarityComponent extends ItemComponent {

    private final @Nullable ResolvableEnum<ItemRarity> rarity;

    public RarityComponent(@Nullable ResolvableEnum<ItemRarity> rarity) {
        this.rarity = rarity;
    }

    public @Nullable ResolvableEnum<ItemRarity> getRarity() {
        return this.rarity;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            Resolvable.applyResolvable(context, this.rarity, itemMeta::setRarity);

            itemStack.setItemMeta(itemMeta);
        }
    }

}
