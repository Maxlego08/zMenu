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
public class MaxDamageComponent extends ItemComponent {

    private final ResolvableInt maxDamage;

    public MaxDamageComponent(int maxDamage) {
        this.maxDamage = ResolvableInt.of(maxDamage);
    }

    public MaxDamageComponent(ResolvableInt maxDamage) {
        this.maxDamage = maxDamage;
    }

    public ResolvableInt getMaxDamage() {
        return this.maxDamage;
    }

    @Override
    public @Nullable Object serialize() {
        return this.maxDamage.serialize();
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        this.applyResolvable(context, maxDamage -> itemStack.setData(DataComponentTypes.MAX_DAMAGE, maxDamage), this.maxDamage);
    }

}
