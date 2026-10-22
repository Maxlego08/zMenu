package fr.maxlego08.menu.api.loader.animation.slot;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Builds a {@link SlotAnimation} from the options written next to the {@code type} of a group.
 * It is what the slot animation registry holds, under its {@link #key() key} and its
 * {@link #aliases() aliases}.
 *
 * <p>Most animations ignore those options entirely, but some, such as the one drawn with a
 * matrix, are configured per inventory.</p>
 *
 * <p>Register one with
 * {@link fr.maxlego08.menu.api.InventoryManager#registerSlotAnimation(org.bukkit.plugin.Plugin, SlotAnimationLoader)}:</p>
 *
 * <pre>
 * public class MyAnimationLoader extends SlotAnimationLoader {
 *     public MyAnimationLoader() {
 *         super(Key.key("myplugin", "my_animation"));
 *     }
 *
 *     public SlotAnimation create(Map&lt;String, Object&gt; options) {
 *         return new MyAnimation();
 *     }
 * }
 * </pre>
 */
public abstract class SlotAnimationLoader implements Keyed {

    private final Key key;
    private final List<Key> aliases;

    /**
     * @param key     the name menus use as {@code type}
     * @param aliases other names for the same animation
     */
    protected SlotAnimationLoader(@NotNull Key key, @NotNull Key... aliases) {
        this.key = key;
        this.aliases = List.of(aliases);
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    /**
     * @return the other names of this animation
     */
    public @NotNull List<Key> aliases() {
        return this.aliases;
    }

    /**
     * Builds the animation.
     *
     * @param options the values of the section the {@code type} was read from
     * @return the animation
     * @throws IllegalArgumentException if the options are missing or invalid, with a message meant
     *                                  to be shown to whoever wrote the file
     */
    @NotNull
    public abstract SlotAnimation create(@NotNull Map<String, Object> options);
}
