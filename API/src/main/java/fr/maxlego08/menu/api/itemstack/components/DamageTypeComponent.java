package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDamageType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DamageTypeComponent extends ItemComponent {

    private final @NotNull ResolvableDamageType damageType;

    public DamageTypeComponent(@NotNull ResolvableDamageType damageType) {
        this.damageType = damageType;
    }

    public @NotNull ResolvableDamageType getDamageType() {
        return this.damageType;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        DamageType resolvedDamageType = Resolvable.resolve(context, this.damageType);
        if (resolvedDamageType != null) {
            itemStack.setData(DataComponentTypes.DAMAGE_TYPE, resolvedDamageType);
        }
    }

}
