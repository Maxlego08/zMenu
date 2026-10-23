package fr.maxlego08.menu.animation.compile;

/**
 * What an {@link AnimationStep} does to its slot.
 */
public enum StepAction {

    /**
     * Displays the real item built by the button owning the slot. This is what the opening
     * animation does when it reaches a slot it had hidden.
     */
    REVEAL,

    /**
     * Displays an item that belongs to the animation itself, such as a placeholder or a temporary
     * item. The slot stays owned by the animation.
     */
    SET,

    /**
     * Empties the slot. The slot stays owned by the animation.
     */
    CLEAR
}
