package fr.maxlego08.menu.animation.compile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The result of compiling the animation of an inventory: everything the engine has to play, and the
 * boundaries the steps were laid out against.
 *
 * @param steps  the steps to play, sorted by time then by slot
 * @param timing the boundaries of the groups
 */
public record CompiledAnimation(@NotNull List<AnimationStep> steps, @NotNull AnimationTiming timing) {

    /**
     * An animation with nothing to play.
     */
    public static final CompiledAnimation EMPTY = new CompiledAnimation(List.of(), AnimationTiming.EMPTY);

    public CompiledAnimation {
        steps = List.copyOf(steps);
    }

    /**
     * Returns whether there is nothing to play.
     *
     * @return true if the animation has no step
     */
    public boolean isEmpty() {
        return this.steps.isEmpty();
    }

    /**
     * Gets the time at which the last step happens.
     *
     * @return the duration in milliseconds, 0 if there is no step
     */
    public long duration() {
        return this.steps.isEmpty() ? 0 : this.steps.getLast().time();
    }
}
