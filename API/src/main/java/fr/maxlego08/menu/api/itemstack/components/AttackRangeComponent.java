package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class AttackRangeComponent extends ItemComponent {

    protected final Resolvable<Float> minReach;
    protected final Resolvable<Float> maxReach;
    protected final Resolvable<Float> minCreativeReach;
    protected final Resolvable<Float> maxCreativeReach;
    protected final Resolvable<Float> hitboxMargin;
    protected final Resolvable<Float> mobFactor;

    protected AttackRangeComponent(
            @NotNull Resolvable<Float> minReach,
            @NotNull Resolvable<Float> maxReach,
            @NotNull Resolvable<Float> minCreativeReach,
            @NotNull Resolvable<Float> maxCreativeReach,
            @NotNull Resolvable<Float> hitboxMargin,
            @NotNull Resolvable<Float> mobFactor) {
        this.minReach = minReach;
        this.maxReach = maxReach;
        this.minCreativeReach = minCreativeReach;
        this.maxCreativeReach = maxCreativeReach;
        this.hitboxMargin = hitboxMargin;
        this.mobFactor = mobFactor;
    }

    public Resolvable<Float> getMinReach() {
        return this.minReach;
    }

    public Resolvable<Float> getMaxReach() {
        return this.maxReach;
    }

    public Resolvable<Float> getMinCreativeReach() {
        return this.minCreativeReach;
    }

    public Resolvable<Float> getMaxCreativeReach() {
        return this.maxCreativeReach;
    }

    public Resolvable<Float> getHitboxMargin() {
        return this.hitboxMargin;
    }

    public Resolvable<Float> getMobFactor() {
        return this.mobFactor;
    }

    protected void applyDataComponent(@NotNull Consumer<Float> consumer, @NotNull BuildContext context, @NotNull Resolvable<Float> resolvable) {
        Float value = resolvable.resolve(context);
        if (value != null) {
            consumer.accept(value);
        }
    }
}