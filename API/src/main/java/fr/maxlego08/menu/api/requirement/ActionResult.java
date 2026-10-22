package fr.maxlego08.menu.api.requirement;

/**
 * Tells the caller whether the actions after this one should still run.
 *
 * <p>Most actions are independent, so they report {@link #CONTINUE} and the list carries on. An
 * action that represents a precondition for the ones after it, the clearest example being taking a
 * player's money, reports {@link #STOP} when it did not succeed, so a list such as "take 1000
 * coins" followed by "give a diamond" cannot hand over the diamond when the payment failed.</p>
 */
public enum ActionResult {

    /**
     * Keep running the remaining actions.
     */
    CONTINUE,

    /**
     * Do not run any of the remaining actions in this list.
     */
    STOP
}
