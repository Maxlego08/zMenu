package fr.maxlego08.menu.storage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * The threads a storage provider works on.
 *
 * <p>Writes run on a single thread, in the order they are asked for: a delete can never overtake
 * the write it undoes. Reads run on a small pool of their own, so a slow read does not hold back
 * the writes. Once closing, work runs on the calling thread instead of being dropped.</p>
 */
public final class StorageThreads {

    private static final int READER_THREADS = 2;

    private final ExecutorService writer;
    private final ExecutorService readers;

    public StorageThreads(String name) {
        this.writer = Executors.newSingleThreadExecutor(threads("zMenu " + name + " Writer"));
        this.readers = Executors.newFixedThreadPool(READER_THREADS, threads("zMenu " + name + " Reader"));
    }

    private static ThreadFactory threads(String name) {
        AtomicInteger counter = new AtomicInteger();
        return runnable -> {
            Thread thread = new Thread(runnable, name + " #" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    /**
     * Queues a write behind every write already queued.
     */
    public CompletableFuture<Void> write(Runnable task) {
        return this.writeAndGet(() -> {
            task.run();
            return null;
        });
    }

    /**
     * Queues a write that produces a result, behind every write already queued.
     */
    public <T> CompletableFuture<T> writeAndGet(Supplier<T> task) {
        return submit(this.writer, task);
    }

    public <T> CompletableFuture<T> read(Supplier<T> task) {
        return submit(this.readers, task);
    }

    private static <T> CompletableFuture<T> submit(ExecutorService executor, Supplier<T> task) {
        try {
            return CompletableFuture.supplyAsync(task, executor);
        } catch (RejectedExecutionException exception) {
            try {
                return CompletableFuture.completedFuture(task.get());
            } catch (RuntimeException failure) {
                return CompletableFuture.failedFuture(failure);
            }
        }
    }

    /**
     * Waits for the queued writes, then stops the threads.
     *
     * @return whether every write finished before the timeout
     */
    public boolean close(long timeout, TimeUnit unit) throws InterruptedException {
        this.readers.shutdown();
        this.writer.shutdown();
        boolean finished = this.writer.awaitTermination(timeout, unit);
        this.readers.awaitTermination(timeout, unit);
        return finished;
    }
}
