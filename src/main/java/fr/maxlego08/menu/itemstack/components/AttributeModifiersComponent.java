package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
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

@SuppressWarnings("unused")
public class AttributeModifiersComponent extends ItemComponent {

    private final List<Tuples<@NotNull Attribute,@NotNull AttributeModifier>> modifiers;

    public AttributeModifiersComponent(List<Tuples<@NotNull Attribute,@NotNull AttributeModifier>> modifiers) {
        this.modifiers = modifiers;
    }

    public List<Tuples<@NotNull Attribute,@NotNull AttributeModifier>> getModifiers() {
        return this.modifiers;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        for (Tuples<Attribute, AttributeModifier> tuple : this.modifiers) {
            itemMeta.addAttributeModifier(tuple.getFirst(), tuple.getSecond());
        }
        itemStack.setItemMeta(itemMeta);
    }

}
