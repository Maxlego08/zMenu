package fr.maxlego08.menu.common.network;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.maxlego08.menu.api.MenuPlugin;
import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

public class PacketQueue<T extends Packet<?>> {

    private final Queue<T> queue;
    private final Map<Class<? extends T>, Consumer<? super T>> handlers;
    private final Consumer<? super T> fallback;
    private final boolean discard;
    private final boolean directDispatch;
    private WrappedTask task;

    private PacketQueue(Builder<T> builder) {
        this.queue          = builder.concurrent ? new ConcurrentLinkedQueue<>() : new LinkedList<>();
        this.handlers       = Map.copyOf(builder.handlers);
        this.fallback       = builder.fallback;
        this.discard        = builder.discard;
        this.directDispatch = builder.directDispatch;
    }

    // ──────────────────────────────────────────────────────────────
    // Queue access
    // ──────────────────────────────────────────────────────────────

    /** Raw queue — hand this to MenuPacketListener if needed. */
    public Queue<T> queue() {
        return queue;
    }

    // ──────────────────────────────────────────────────────────────
    // Offer
    // ──────────────────────────────────────────────────────────────

    /**
     * Called by MenuPacketListener on the Netty I/O thread.
     * <p>
     * If {@code discard} is enabled the packet is silently dropped.
     * If {@code directDispatch} is enabled the handler fires immediately
     * on the Netty thread — only safe for thread-safe, Bukkit-API-free handlers.
     * Otherwise the packet is queued for later {@link #dispatch()} on the main thread.
     */
    public void offer(T packet) {
        if (discard) return;
        if (directDispatch) {
            dispatchSingle(packet);
            return;
        }
        queue.offer(packet);
    }

    // ──────────────────────────────────────────────────────────────
    // Dispatch
    // ──────────────────────────────────────────────────────────────

    /**
     * Drain every queued packet and fire the matching consumer.
     * Must be called on the correct thread (main / entity thread).
     * No-op when {@code directDispatch} is enabled since packets never queue.
     */
    public void dispatch() {
        T packet;
        while ((packet = queue.poll()) != null) dispatchSingle(packet);
    }

    /**
     * Drain exactly one queued packet.
     * Returns {@code true} if a packet was dispatched.
     */
    public boolean dispatchOne() {
        T packet = queue.poll();
        if (packet == null) return false;
        dispatchSingle(packet);
        return true;
    }

    @SuppressWarnings("unchecked")
    private void dispatchSingle(T packet) {
        Consumer<? super T> handler = handlers.get(packet.getClass());
        if (handler != null) handler.accept(packet);
        else if (fallback != null) fallback.accept(packet);
    }

    // ──────────────────────────────────────────────────────────────
    // Scheduling
    // ──────────────────────────────────────────────────────────────

    /**
     * Schedules a repeating task that calls {@link #dispatch()} every tick.
     * The queue owns the task lifecycle — call {@link #cancel()} to stop it.
     * <p>
     * Accepts a scheduler function to keep PacketQueue decoupled from FoliaLib:
     * <pre>{@code
     * queue.schedule(r -> plugin.getScheduler().runAtEntityTimer(player, r, 1L, 1L));
     * }</pre>
     */
    public PacketQueue<T> schedule(Function<Runnable, WrappedTask> scheduler) {
        this.task = scheduler.apply(this::dispatch);
        return this;
    }

    /**
     * Convenience overload when you don't need a custom interval.
     * Schedules at 1-tick period on the player's entity thread.
     */
    public PacketQueue<T> schedule(MenuPlugin plugin, Player player) {
        return schedule(r -> plugin.getScheduler().runAtEntityTimer(player, r, 1L, 1L));
    }

    /** Cancels the scheduled dispatch task if one was started via {@link #schedule}. */
    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Utility
    // ──────────────────────────────────────────────────────────────

    public boolean isEmpty() { return queue.isEmpty(); }
    public int size()        { return queue.size(); }
    public void clear()      { queue.clear(); }

    // ──────────────────────────────────────────────────────────────
    // Static factories
    // ──────────────────────────────────────────────────────────────

    /**
     * Silently discards every packet — useful to block a packet type
     * without processing it.
     */
    public static <T extends Packet<?>> PacketQueue<T> empty() {
        return new PacketQueue<>(new Builder<T>().discard(true).concurrent(false));
    }

    /**
     * Packets are queued but never dispatched automatically.
     * Drain manually via {@link #dispatch()} or {@link #dispatchOne()}.
     */
    public static <T extends Packet<?>> PacketQueue<T> unhandled() {
        return PacketQueue.<T>builder().build();
    }

    /**
     * Queue with a single catch-all handler for every packet type.
     */
    public static <T extends Packet<?>> PacketQueue<T> of(Consumer<? super T> handler) {
        return PacketQueue.<T>builder().orElse(handler).build();
    }

    /**
     * Queue with a single typed handler.
     */
    public static <P extends Packet<?>> PacketQueue<P> of(Class<P> type, Consumer<? super P> handler) {
        return PacketQueue.<P>builder().on(type, handler).build();
    }

    public static <T extends Packet<?>> Builder<T> builder() {
        return new Builder<>();
    }

    // ──────────────────────────────────────────────────────────────
    // Builder
    // ──────────────────────────────────────────────────────────────

    public static final class Builder<T extends Packet<?>> {

        private boolean concurrent      = true;
        private boolean discard         = false;
        private boolean directDispatch  = false;
        private final Map<Class<? extends T>, Consumer<? super T>> handlers = new LinkedHashMap<>();
        private Consumer<? super T> fallback;

        public Builder<T> concurrent(boolean concurrent) {
            this.concurrent = concurrent;
            return this;
        }

        /**
         * When {@code true}, every incoming packet is silently dropped.
         * No handlers are called and nothing is queued.
         */
        public Builder<T> discard(boolean discard) {
            this.discard = discard;
            return this;
        }

        /**
         * When {@code true}, handlers fire immediately on the Netty I/O thread
         * instead of being queued for the main thread.
         * <p>
         * <b>Only enable this if your handlers are thread-safe and do not touch
         * the Bukkit API.</b> Using this with {@link org.bukkit.entity.Player}
         * methods or any scheduler will cause corruption or crashes.
         */
        public Builder<T> directDispatch(boolean directDispatch) {
            this.directDispatch = directDispatch;
            return this;
        }

        /**
         * Register a typed handler for a specific packet subclass.
         * <pre>{@code
         * .on(ServerboundRenameItemPacket.class, p -> handleRename(p.getName()))
         * }</pre>
         */
        @SuppressWarnings("unchecked")
        public <P extends T> Builder<T> on(Class<P> type, Consumer<? super P> handler) {
            handlers.put(type, (Consumer<? super T>) handler);
            return this;
        }

        /**
         * Catch-all — fires for any packet whose class has no specific handler.
         */
        public Builder<T> orElse(Consumer<? super T> fallback) {
            this.fallback = fallback;
            return this;
        }

        public PacketQueue<T> build() {
            return new PacketQueue<>(this);
        }
    }
}