package fr.maxlego08.menu.api.button;

import java.util.Collection;

/**
 * <p>Allows to have multiple slots for a single button</p>
 */
public interface SlotButton {

    /**
     * Return the list of slots
     *
     * @return slots
     */
	Collection<Integer> getSlots();

}
