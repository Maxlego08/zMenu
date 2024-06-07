package fr.maxlego08.menu.api.scheduler;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Timer;

/**
 * <p>The ZScheduler interface provides methods for managing tasks and working with <a href="https://papermc.io/software/folia">Folia</a>.</p>
 */
public interface ZScheduler {

    /**
     * The default timer used for scheduling tasks.
     */
    Timer TIMER = new Timer();

    /**
     * Runs the task.
     *
     * @param location Required for Folia, in Bukkit can be null.
     * @param task     The task to run.
     * @return The created {@link ZScheduler}.
     */
    @NotNull ZScheduler runTask(@Nullable Location location, Runnable task);

    /**
     * Runs the task asynchronously.
     *
     * @param task The task to run.
     * @return The created {@link ZScheduler}.
     */
    @NotNull ZScheduler runTaskAsynchronously(Runnable task);

    /**
     * Runs the task after a specified number of ticks.
     *
     * @param location Required for Folia, in Bukkit can be null.
     * @param task     The task to run.
     * @param delay    The number of ticks to wait.
     * @return The created {@link ZScheduler}.
     */
    @NotNull ZScheduler runTaskLater(@Nullable Location location, long delay, Runnable task);

    /**
     * Runs the task asynchronously after a specified number of ticks.
     *
     * @param task  The task to run.
     * @param delay The number of ticks to wait.
     * @return The created {@link ZScheduler}.
     */
    @NotNull ZScheduler runTaskLaterAsynchronously(long delay, Runnable task);

    /**
     * Runs the task repeatedly on a timer.
     *
     * @param location Required for Folia, in Bukkit can be null.
     * @param task     The task to run.
     * @param delay    The delay before the task is first run (in ticks).
     * @param period   The ticks elapsed before the task is run again.
     * @return The created {@link ZScheduler}.
     */
    @NotNull ZScheduler runTaskTimer(@Nullable Location location, long delay, long period, Runnable task);

    /**
     * Runs the task repeatedly on a timer asynchronously.
     *
     * @param task   The task to run.
     * @param delay  The delay before the task is first run (in ticks).
     * @param period The ticks elapsed before the task is run again.
     * @return The created {@link ZScheduler}.
     */
    @NotNull ZScheduler runTaskTimerAsynchronously(long delay, long period, Runnable task);

    /**
     * Cancels the task.
     */
    void cancel();

    boolean isFolia();
}
