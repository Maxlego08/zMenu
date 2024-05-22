package fr.maxlego08.menu.scheduler;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.FoliaImplementation;
import com.tcoded.folialib.impl.ServerImplementation;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class FoliaScheduler implements ZScheduler {
    private final FoliaLib foliaLib;
    private final ServerImplementation serverImplementation;
    JavaPlugin plugin;
    private WrappedTask task;

    public FoliaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.foliaLib = new FoliaLib(plugin);
        this.serverImplementation = foliaLib.getImpl();
    }

    @Override
    public @NotNull ZScheduler runTask(Location location, Runnable task) {
        if (location != null) {
            serverImplementation.runAtLocation(location, w -> task.run());
        } else {
            serverImplementation.runNextTick(w -> task.run());
        }
        return this;
    }

    @Override
    public @NotNull ZScheduler runTaskAsynchronously(Runnable task) {
        serverImplementation.runAsync(w -> task.run());
        return this;
    }

    @Override
    public @NotNull ZScheduler runTaskLater(Location location, long delay, Runnable task) {
        if (location != null) {
            this.task = serverImplementation.runAtLocationLater(location, task, delay);
        } else {
            this.task = serverImplementation.runLater(task, delay);
        }
        return this;
    }

    @Override
    public @NotNull ZScheduler runTaskLaterAsynchronously(long delay, Runnable task) {
        this.task = serverImplementation.runLater(task, delay);
        return this;
    }

    @Override
    public @NotNull ZScheduler runTaskTimer(Location location, long delay, long period, Runnable task) {
        this.task = serverImplementation.runAtLocationTimer(location, task, delay, period);
        return this;
    }

    @Override
    public @NotNull ZScheduler runTaskTimerAsynchronously(long delay, long period, Runnable task) {
        this.task = serverImplementation.runTimerAsync(task, delay, period);
        return this;
    }

    @Override
    public void cancel() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    @Override
    public boolean isFolia() {
        return this.serverImplementation instanceof FoliaImplementation;
    }
}
