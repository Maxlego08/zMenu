package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DamageComponent extends ItemComponent {

    private final ResolvableInt damage;

    public DamageComponent(int damage) {
        this.damage = ResolvableInt.of(damage);
    }

    public DamageComponent(ResolvableInt damage) {
        this.damage = damage;
    }

    public ResolvableInt getDamage() {
        return this.damage;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        this.applyResolvable(context, damage -> itemStack.setData(DataComponentTypes.DAMAGE, damage), this.damage);
    }

    @Override
    public @Nullable Object serialize() {
        return this.damage.serialize();
    }

}
