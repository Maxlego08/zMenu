package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

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

    /**
     * Writes the {@code type} of this animation and its options, the way its
     * {@link fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader} reads them back.
     *
     * @param section the section of the group the animation belongs to
     * @throws UnsupportedOperationException if this animation has no type a file can name, such as
     *                                       one built in code
     * @deprecated use {@link #serialize(Section)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default void serialize(@NotNull ConfigurationSection section) {
        throw new UnsupportedOperationException(this.getClass().getName() + " cannot be serialized");
    }

    /**
     * Writes this object's keys into a section. zMenu calls this method, override it.
     * By default it writes through the deprecated method, which existing addons override.
     * <p>BoostedYAML is loaded by zMenu: an addon that calls this method must also declare it.
     */
    default void serialize(@NotNull Section section) {
        YamlSections.write(section, copy -> this.serialize(copy));
    }
}
