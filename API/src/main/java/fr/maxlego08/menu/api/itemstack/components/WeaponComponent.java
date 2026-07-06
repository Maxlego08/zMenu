package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Weapon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class WeaponComponent extends ItemComponent {
    private final ResolvableInt itemDamagePerAttack;
    private final ResolvableFloat disableBlockingForSeconds;

    public WeaponComponent(int itemDamagePerAttack, float disableBlockingForSeconds) {
        this.itemDamagePerAttack = ResolvableInt.of(itemDamagePerAttack);
        this.disableBlockingForSeconds = ResolvableFloat.of(disableBlockingForSeconds);
    }

    public WeaponComponent(ResolvableInt itemDamagePerAttack, ResolvableFloat disableBlockingForSeconds) {
        this.itemDamagePerAttack = itemDamagePerAttack;
        this.disableBlockingForSeconds = disableBlockingForSeconds;
    }

    public ResolvableInt getItemDamagePerAttack() {
        return this.itemDamagePerAttack;
    }

    public ResolvableFloat getDisableBlockingForSeconds() {
        return this.disableBlockingForSeconds;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Weapon.Builder weapon = Weapon.weapon();

        Resolvable.applyResolvable(context, this.itemDamagePerAttack, weapon::itemDamagePerAttack);
        Resolvable.applyResolvable(context, this.disableBlockingForSeconds, weapon::disableBlockingForSeconds);

        itemStack.setData(DataComponentTypes.WEAPON, weapon.build());
    }
}
