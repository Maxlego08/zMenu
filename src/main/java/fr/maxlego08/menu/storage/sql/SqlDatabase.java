package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.RequestHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * An open SQL connection and the threads that use it.
 *
 * <p>Writes run on a single thread, in the order they are asked for: a delete can never overtake
 * the insert it undoes. Reads run on a small pool of their own, so a slow read does not hold back
 * the writes.</p>
 */
final class SqlDatabase {

    private static final int READER_THREADS = 2;

    private final DatabaseConnection connection;
    private final RequestHelper requestHelper;
    private final ExecutorService writer = Executors.newSingleThreadExecutor(threads("zMenu Storage Writer"));
    private final ExecutorService readers = Executors.newFixedThreadPool(READER_THREADS, threads("zMenu Storage Reader"));

    SqlDatabase(DatabaseConnection connection, RequestHelper requestHelper) {
        this.connection = connection;
        this.requestHelper = requestHelper;
    }

    private static ThreadFactory threads(String name) {
        AtomicInteger counter = new AtomicInteger();
        return runnable -> {
            Thread thread = new Thread(runnable, name + " #" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    RequestHelper requestHelper() {
        return this.requestHelper;
    }

    /**
     * Queues a write behind every write already queued. Once the database is closing, the write
     * runs on the calling thread instead of being dropped.
     */
    CompletableFuture<Void> write(Runnable task) {
        try {
            return CompletableFuture.runAsync(task, this.writer);
        } catch (RejectedExecutionException exception) {
            try {
                task.run();
                return CompletableFuture.completedFuture(null);
            } catch (RuntimeException failure) {
                return CompletableFuture.failedFuture(failure);
            }
        }
    }

    <T> CompletableFuture<T> read(Supplier<T> task) {
        try {
            return CompletableFuture.supplyAsync(task, this.readers);
        } catch (RejectedExecutionException exception) {
            try {
                return CompletableFuture.completedFuture(task.get());
            } catch (RuntimeException failure) {
                return CompletableFuture.failedFuture(failure);
            }
        }
    }

    /**
     * Waits for the queued writes, then releases the threads and the connection.
     *
     * @return whether every write finished before the timeout
     */
    boolean close(long timeout, TimeUnit unit) throws InterruptedException {
        this.readers.shutdown();
        this.writer.shutdown();
        boolean finished = this.writer.awaitTermination(timeout, unit);
        this.readers.awaitTermination(timeout, unit);
        this.connection.disconnect();
        return finished;
    }
}
