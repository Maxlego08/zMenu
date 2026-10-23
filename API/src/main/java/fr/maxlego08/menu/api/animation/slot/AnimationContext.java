package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationVariant;
import fr.maxlego08.menu.api.animation.slot.settings.item.ItemChoice;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * What a {@link SlotAnimation} is being asked to order, and who for.
 *
 * <p>The player and the inventory are known when an animation is compiled but not when it is
 * written, so an ordering may depend on them: a different order per player, a seed that stays the
 * same for one player, or an order that reads something from the menu itself.</p>
 *
 * <p>They are null when an animation is compiled outside of an inventory, such as in a test, so an
 * implementation using them must cope with their absence.</p>
 *
 * @param inventorySize the number of slots the animation is laid out over: the menu and the player
 *                      inventory together, not the size of the menu alone. See {@link AnimationSlots}
 * @param slots         the slots to order
 * @param player        the player the animation is played for, if there is one
 * @param inventory     the menu being animated, if there is one
 * @param random        the source every roll of the animation is taken from
 */
public record AnimationContext(
        int inventorySize,
        @NotNull List<Integer> slots,
        @Nullable Player player,
        @Nullable Inventory inventory,
        @NotNull Random random
) {

    public AnimationContext {
        slots = List.copyOf(slots);
    }

    public AnimationContext(int inventorySize, @NotNull List<Integer> slots, @Nullable Player player, @Nullable Inventory inventory) {
        this(inventorySize, slots, player, inventory, ThreadLocalRandom.current());
    }

    /**
     * Rolls a chance.
     *
     * <p>Everything an animation leaves to chance goes through here, so a test giving the context a
     * seeded {@link Random} gets the same animation every time.</p>
     *
     * @param chance the chance in percent, 100 always passing and 0 never
     * @return true if the roll passed
     */
    public boolean roll(float chance) {
        if (chance >= 100) return true;
        if (chance <= 0) return false;

        return this.random.nextDouble() * 100 < chance;
    }

    /**
     * Picks a number of entries at random, without repeating any of them.
     *
     * @param values the entries to pick from
     * @param count  how many to pick; anything from the size of the list upwards returns them all
     * @param <T>    the type of the entries
     * @return the entries picked, in the order they were given
     */
    @NotNull
    public <T> List<T> pickSome(@NotNull List<T> values, int count) {
        if (count <= 0 || count >= values.size()) return List.copyOf(values);

        List<T> shuffled = new ArrayList<>(values);
        Collections.shuffle(shuffled, this.random);

        List<T> picked = new ArrayList<>(shuffled.subList(0, count));
        picked.sort(Comparator.comparingInt(values::indexOf));
        return picked;
    }

    /**
     * Picks one of the variants of an animation, respecting their weights.
     *
     * @param variants the variants to pick from
     * @return the variant picked, or null if there is nothing to pick from
     */
    @Nullable
    public AnimationVariant pickVariant(@NotNull List<AnimationVariant> variants) {
        if (variants.isEmpty()) return null;
        if (variants.size() == 1) return variants.getFirst();

        int total = 0;
        for (AnimationVariant variant : variants) {
            total += variant.weight();
        }

        int roll = this.random.nextInt(total);
        for (AnimationVariant variant : variants) {
            roll -= variant.weight();
            if (roll < 0) return variant;
        }
        return variants.getLast();
    }

    @Nullable
    public MenuItemStack pick(@NotNull List<ItemChoice> choices) {
        if (choices.isEmpty()) return null;
        if (choices.size() == 1) return choices.getFirst().item();

        int total = 0;
        for (ItemChoice choice : choices) {
            total += choice.weight();
        }

        int roll = this.random.nextInt(total);
        for (ItemChoice choice : choices) {
            roll -= choice.weight();
            if (roll < 0) return choice.item();
        }
        return choices.getLast().item();
    }

    /**
     * The same context, for another set of slots.
     *
     * @param slots the slots to order
     * @return the new context
     */
    @NotNull
    public AnimationContext withSlots(@NotNull List<Integer> slots) {
        return new AnimationContext(this.inventorySize, slots, this.player, this.inventory, this.random);
    }
}
