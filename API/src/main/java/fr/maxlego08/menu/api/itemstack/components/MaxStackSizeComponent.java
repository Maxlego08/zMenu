package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;

import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;

public abstract class MaxStackSizeComponent extends ItemComponent {
    protected final ResolvableInt maxStackSize;

    public MaxStackSizeComponent(int maxStackSize) {
        this.maxStackSize = ResolvableInt.of(maxStackSize);
    }

    public MaxStackSizeComponent(ResolvableInt maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public ResolvableInt getMaxStackSize() {
        return this.maxStackSize;
    }
}
