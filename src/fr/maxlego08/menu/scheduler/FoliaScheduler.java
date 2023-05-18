package fr.maxlego08.menu.scheduler;

import fr.maxlego08.menu.api.scheduler.ZScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class FoliaScheduler implements ZScheduler {

    @Nullable ScheduledTask scheduledTask;

    JavaPlugin plugin;

    public FoliaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ZScheduler runTask(Location location, Runnable task) {
        if (location != null) {
            plugin.getServer().getRegionScheduler().execute(plugin, location, task);
        } else {
            plugin.getServer().getGlobalRegionScheduler().execute(plugin, task);
        }
        return this;
    }

    @Override
    public @NotNull ZScheduler runTaskAsynchronously(Runnable task) {
        scheduledTask = plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask1 -> task.run());
        return this;
    }

    @Override
    public @NotNull ZScheduler runTaskLater(Location location, long delay, Runnable task) {
        if (location != null) {
            scheduledTask = plugin.getServer().getRegionScheduler().runDelayed(plugin, location, scheduledTask1 -> task.run(), delay);
        } else {
            scheduledTask = plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask1 -> task.run(), delay);
        }
        return this;
    }

    @Override
    public @NotNull ZScheduler runTaskLaterAsynchronously(long delay, Runnable task) {
        scheduledTask = plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduledTask1 -> task.run(), delay, TimeUnit.SECONDS);
        return this;
    }

    @Override
    public @NotNull ZScheduler runTaskTimer(Location location, long delay, long period, Runnable task) {
        scheduledTask = plugin.getServer().getRegionScheduler().runAtFixedRate(plugin, location, scheduledTask1 -> task.run(), delay, period);
        return this;
    }

    @Override
    public @NotNull ZScheduler runTaskTimerAsynchronously(long delay, long period, Runnable task) {
        scheduledTask = plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, scheduledTask1 -> task.run(), delay, period, TimeUnit.SECONDS);
        return this;
    }

    @Override
    public void cancel() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel();
        }
    }

}
