package fr.maxlego08.menu.api.scheduler;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Timer;

public interface ZScheduler {

    Timer TIMER = new Timer();

    /**
     * Run the task.
     *
     * @param task task...
     * @param location required for Folia, in Bukkit can be null
     * @return The created {@link ZScheduler}.
     */
    @NotNull ZScheduler runTask(@Nullable Location location, Runnable task);

    /**
     * Run the task asynchronously.
     *
     * @param task task...
     * @return The created {@link ZScheduler}
     */
    @NotNull ZScheduler runTaskAsynchronously(Runnable task);

    /**
     * Run the task after a specified number of ticks.
     *
     * @param location required for Folia, in Bukkit can be null
     * @param task task...
     * @param delay The number of ticks to wait.
     * @return The created {@link ZScheduler}
     */
    @NotNull ZScheduler runTaskLater(@Nullable Location location, long delay, Runnable task);

    /**
     * Run the task asynchronously after a specified number of ticks.
     *
     * @param task task...
     * @param delay The number of ticks to wait.
     * @return The created {@link ZScheduler}
     */
    @NotNull ZScheduler runTaskLaterAsynchronously(long delay, Runnable task);

    /**
     * Run the task repeatedly on a timer.
     *
     * @param location required for Folia, in Bukkit can be null
     * @param task task...
     * @param delay  The delay before the task is first run (in ticks).
     * @param period The ticks elapsed before the task is run again.
     * @return The created {@link ZScheduler}
     */
    @NotNull ZScheduler runTaskTimer(@Nullable Location location, long delay, long period, Runnable task);

    /**
     * Run the task repeatedly on a timer asynchronously.
     *
     * @param task task...
     * @param delay  The delay before the task is first run (in ticks).
     * @param period The ticks elapsed before the task is run again.
     * @return The created {@link ZScheduler}
     */
    @NotNull ZScheduler runTaskTimerAsynchronously(long delay, long period, Runnable task);

    /**
     * Cancel the task.
     */
    void cancel();
}