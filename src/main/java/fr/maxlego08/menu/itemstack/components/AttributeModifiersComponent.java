package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.Tuples;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record AttributeModifiersComponent(
        List<Tuples<@NotNull Attribute,@NotNull AttributeModifier>> modifiers
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        for (Tuples<Attribute, AttributeModifier> tuple : this.modifiers) {
            itemMeta.addAttributeModifier(tuple.getFirst(), tuple.getSecond());
        }
        itemStack.setItemMeta(itemMeta);
    }
}
