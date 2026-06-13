package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;

public abstract class AttackRangeComponent extends ItemComponent {
    protected final float minReach;
    protected final float maxReach;
    protected final float minCreativeReach;
    protected final float maxCreativeReach;
    protected final float hitboxMargin;
    protected final float mobFactor;


    protected AttackRangeComponent(float minReach, float maxReach, float minCreativeReach, float maxCreativeReach, float hitboxMargin, float mobFactor) {
        this.minReach = minReach;
        this.maxReach = maxReach;
        this.minCreativeReach = minCreativeReach;
        this.maxCreativeReach = maxCreativeReach;
        this.hitboxMargin = hitboxMargin;
        this.mobFactor = mobFactor;
    }

    public float getMinReach() {
        return this.minReach;
    }

    public float getMaxReach() {
        return this.maxReach;
    }

    public float getMinCreativeReach() {
        return this.minCreativeReach;
    }

    public float getMaxCreativeReach() {
        return this.maxCreativeReach;
    }

    public float getHitboxMargin() {
        return this.hitboxMargin;
    }

    public float getMobFactor() {
        return this.mobFactor;
    }
}
