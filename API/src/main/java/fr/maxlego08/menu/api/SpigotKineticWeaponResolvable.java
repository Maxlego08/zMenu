package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.itemstack.ZKineticWeaponCondition;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableKineticCondition;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.inventory.meta.components.KineticWeaponComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpigotKineticWeaponResolvable extends ResolvableKineticCondition<KineticWeaponComponent.Condition> {

    public SpigotKineticWeaponResolvable(@Nullable ResolvableInt maxDurationTicks, @Nullable ResolvableFloat minSpeed, @Nullable ResolvableFloat minRelativeSpeed) {
        super(maxDurationTicks, minSpeed, minRelativeSpeed);
    }

    @NotNull
    @Override
    public KineticWeaponComponent.Condition applyTo(KineticWeaponComponent.@Nullable Condition component, @NotNull BuildContext context) {
        if (component == null) {
            component = new ZKineticWeaponCondition();
        }
        this.applyResolvable(component::setMaxDurationTicks, this.maxDurationTicks, context);
        this.applyResolvable(component::setMinSpeed, this.minSpeed, context);
        this.applyResolvable(component::setMinRelativeSpeed, this.minRelativeSpeed, context);
        return component;
    }
}
