package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import org.jetbrains.annotations.NotNull;

public abstract class AttackRangeComponent extends ItemComponent {

    protected final ResolvableFloat minReach;
    protected final ResolvableFloat maxReach;
    protected final ResolvableFloat minCreativeReach;
    protected final ResolvableFloat maxCreativeReach;
    protected final ResolvableFloat hitboxMargin;
    protected final ResolvableFloat mobFactor;

    protected AttackRangeComponent(
            @NotNull ResolvableFloat minReach,
            @NotNull ResolvableFloat maxReach,
            @NotNull ResolvableFloat minCreativeReach,
            @NotNull ResolvableFloat maxCreativeReach,
            @NotNull ResolvableFloat hitboxMargin,
            @NotNull ResolvableFloat mobFactor) {
        this.minReach = minReach;
        this.maxReach = maxReach;
        this.minCreativeReach = minCreativeReach;
        this.maxCreativeReach = maxCreativeReach;
        this.hitboxMargin = hitboxMargin;
        this.mobFactor = mobFactor;
    }

    public ResolvableFloat getMinReach() {
        return this.minReach;
    }

    public ResolvableFloat getMaxReach() {
        return this.maxReach;
    }

    public ResolvableFloat getMinCreativeReach() {
        return this.minCreativeReach;
    }

    public ResolvableFloat getMaxCreativeReach() {
        return this.maxCreativeReach;
    }

    public ResolvableFloat getHitboxMargin() {
        return this.hitboxMargin;
    }

    public ResolvableFloat getMobFactor() {
        return this.mobFactor;
    }
}