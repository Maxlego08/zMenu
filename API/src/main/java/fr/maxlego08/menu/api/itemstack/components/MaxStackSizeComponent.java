package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;

public abstract class MaxStackSizeComponent extends ItemComponent {
    protected final int maxStackSize;

    public MaxStackSizeComponent(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public int getMaxStackSize() {
        return this.maxStackSize;
    }
}
