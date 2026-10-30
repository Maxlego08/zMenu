package fr.maxlego08.menu.api.button.buttons;

import fr.maxlego08.menu.api.button.Button;

public abstract class ItemDragButton extends Button {

    /**
     * A drag slot is exactly where a placeholder is useful: it shows the player where an item is
     * meant to go while the menu is still opening.
     *
     * <p>The items an animation draws are marked as belonging to the menu, so closing the inventory
     * in the middle of one never hands them to the player.</p>
     *
     * @return true
     */
    @Override
    public boolean allowsTemporaryItems() {
        return true;
    }
}

