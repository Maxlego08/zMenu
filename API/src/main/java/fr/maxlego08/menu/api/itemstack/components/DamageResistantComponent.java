package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Tag;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DamageResistantComponent extends ItemComponent {
    private final @NotNull Tag<DamageType> damageType;

    public DamageResistantComponent(@NotNull Tag<DamageType> damageType) {
        this.damageType = damageType;
    }

    public @NotNull Tag<DamageType> getDamageType() {
        return damageType;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null){
            itemMeta.setDamageResistant(this.damageType);
            itemStack.setItemMeta(itemMeta);
        }
    }
}
