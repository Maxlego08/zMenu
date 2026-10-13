package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.RarityComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyRarityComponent extends RarityComponent {

    public LegacyRarityComponent(@Nullable ResolvableEnum<ItemRarity> rarity) {
        super(rarity);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        this.applyResolvable(context, itemMeta::setRarity, this.getRarity());

        itemStack.setItemMeta(itemMeta);
    }
}
