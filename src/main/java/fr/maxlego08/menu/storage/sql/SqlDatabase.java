package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.storage.StorageThreads;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.RequestHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * An open SQL connection and the threads that use it. Writes are applied in the order they are
 * asked for, see {@link StorageThreads}.
 */
final class SqlDatabase {

    private final DatabaseConnection connection;
    private final RequestHelper requestHelper;
    private final StorageThreads threads = new StorageThreads("Storage");

    SqlDatabase(DatabaseConnection connection, RequestHelper requestHelper) {
        this.connection = connection;
        this.requestHelper = requestHelper;
    }

    RequestHelper requestHelper() {
        return this.requestHelper;
    }

    CompletableFuture<Void> write(Runnable task) {
        return this.threads.write(task);
    }

    <T> CompletableFuture<T> writeAndGet(Supplier<T> task) {
        return this.threads.writeAndGet(task);
    }

    <T> CompletableFuture<T> read(Supplier<T> task) {
        return this.threads.read(task);
    }

    /**
     * Waits for the queued writes, then releases the threads and the connection.
     *
     * @return whether every write finished before the timeout
     */
    boolean close(long timeout, TimeUnit unit) throws InterruptedException {
        boolean finished = this.threads.close(timeout, unit);
        this.connection.disconnect();
        return finished;
    }
}
