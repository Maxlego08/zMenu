package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.damage.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class ResolvableDamageReduction implements Resolvable<DamageReduction> {
    private final Resolvable<RegistryKeySet<DamageType>> type;
    private final ResolvableFloat horizontalBlockingAngle;
    private final ResolvableFloat base;
    private final ResolvableFloat factor;

    public ResolvableDamageReduction(Resolvable<RegistryKeySet<DamageType>> type, ResolvableFloat horizontalBlockingAngle, ResolvableFloat base, ResolvableFloat factor) {
        this.type = type;
        this.horizontalBlockingAngle = horizontalBlockingAngle;
        this.base = base;
        this.factor = factor;
    }

    @Override
    public @NonNull DamageReduction resolve(@NotNull BuildContext context) {
        DamageReduction.Builder builder = DamageReduction.damageReduction();

        Resolvable.applyResolvable(context, this.type, builder::type);
        Resolvable.applyResolvable(context, this.horizontalBlockingAngle, builder::horizontalBlockingAngle);
        Resolvable.applyResolvable(context, this.base, builder::base);
        Resolvable.applyResolvable(context, this.factor, builder::factor);

        return builder.build();
    }
}
