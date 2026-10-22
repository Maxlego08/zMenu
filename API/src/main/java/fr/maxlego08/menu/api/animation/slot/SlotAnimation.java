package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Decides in which order the slots of an inventory are revealed when it opens.
 *
 * <p>An implementation only reorders the slots it is given; the timing and the number of slots
 * revealed per step come from {@link SlotAnimationSettings}. Implementations must return every slot
 * they receive, exactly once, so that no item is ever lost.</p>
 */
public interface SlotAnimation {

    /**
     * Orders the slots to reveal.
     *
     * @param inventorySize the number of slots being laid out: a menu and the player inventory
     *                      together, so 90 for a double chest. Always a multiple of 9 when the menu
     *                      is a chest, since both parts are
     * @param slots         the slots holding an item, in ascending order
     * @return the same slots, in the order they must be revealed
     */
    @NotNull
    List<Integer> order(int inventorySize, @NotNull List<Integer> slots);

    /**
     * Orders the slots, knowing who the animation is played for.
     *
     * <p>This is what the compiler calls. Override it instead of {@link #order(int, List)} when the
     * order depends on the player or on the menu; the default simply forwards, so an implementation
     * that does not care only has to write the simple one.</p>
     *
     * @param context what to order, and who for
     * @return the same slots, in the order they must be revealed
     */
    @NotNull
    default List<Integer> order(@NotNull AnimationContext context) {
        return this.order(context.inventorySize(), context.slots());
    }
}
