package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Reveals the slots in a random order.
 */
public class RandomSlotAnimation implements SlotAnimation {

    private final Random random;

    public RandomSlotAnimation() {
        this(null);
    }

    /**
     * @param random the source of randomness, or null to use {@link ThreadLocalRandom}
     */
    public RandomSlotAnimation(Random random) {
        this.random = random;
    }

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        List<Integer> ordered = new ArrayList<>(slots);
        Collections.shuffle(ordered, this.random == null ? ThreadLocalRandom.current() : this.random);
        return ordered;
    }
}
