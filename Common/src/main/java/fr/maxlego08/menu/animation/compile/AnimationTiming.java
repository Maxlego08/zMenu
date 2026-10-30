package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The boundaries of an animation, produced by the first pass of the compiler and used by the second
 * one to resolve {@link AnimationAnchor}.
 *
 * @param groups the timing of every group that had at least one slot, keyed by group number
 * @param end    the time at which the last group is over, in milliseconds
 */
public record AnimationTiming(@NotNull Map<Integer, GroupTiming> groups, long end) {

    public AnimationTiming {
        groups = Map.copyOf(groups);
    }

    /**
     * An animation with no group at all.
     */
    public static final AnimationTiming EMPTY = new AnimationTiming(Map.of(), 0);

    /**
     * Resolves an anchor to a time.
     *
     * <p>An anchor pointing at a group that holds no slot resolves to the end of the animation,
     * which is the least surprising place to put something the author expected to see after a group
     * that turned out to be empty.</p>
     *
     * @param anchor the anchor to resolve, null resolves to the start
     * @return the time in milliseconds, never negative
     */
    public long resolve(@Nullable AnimationAnchor anchor) {
        if (anchor == null) return 0;

        return switch (anchor.type()) {
            case START -> 0;
            case END -> this.end;
            case MILLIS -> Math.max(0, anchor.millis());
            case GROUP_START -> {
                GroupTiming timing = this.groups.get(anchor.group());
                yield timing == null ? this.end : timing.start();
            }
            case GROUP_END -> {
                GroupTiming timing = this.groups.get(anchor.group());
                yield timing == null ? this.end : timing.end();
            }
        };
    }

    /**
     * Returns whether a group holds at least one slot.
     *
     * @param group the group number
     * @return true if the group exists in this animation
     */
    public boolean hasGroup(int group) {
        return this.groups.containsKey(group);
    }
}
