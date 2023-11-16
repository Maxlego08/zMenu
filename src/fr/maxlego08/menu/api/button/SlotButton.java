package fr.maxlego08.menu.api.button;

import java.util.Collection;

/**
 * <p>The SlotButton interface allows a single button to occupy multiple slots in an inventory.</p>
 */
public interface SlotButton {

    /**
     * Returns the list of slots where the button will be placed.
     *
     * @return The collection of slots.
     */
    Collection<Integer> getSlots();

}
