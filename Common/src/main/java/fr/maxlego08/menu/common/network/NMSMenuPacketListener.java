package fr.maxlego08.menu.common.network;

import fr.maxlego08.menu.api.MenuPlugin;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

/**
 * Intercepts Netty packets per player for ZMenu's custom inventory system.
 * Injected before Minecraft's own packet_handler in the pipeline.
 */
public class NMSMenuPacketListener implements Listener {

    private static final String MC_HANDLER = "packet_handler";
    private static final String MENU_HANDLER = "zmenu_packet_handler";

    private static NMSMenuPacketListener instance;

    private final MenuPlugin plugin;
    private final Map<UUID, MenuPacketHandler> handlers = new ConcurrentHashMap<>();

    private NMSMenuPacketListener(MenuPlugin plugin) {
        this.plugin = plugin;
        // Inject for players already online (e.g. on reload)
        Bukkit.getOnlinePlayers().forEach(this::inject);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void init(MenuPlugin plugin) {
        if (instance != null) throw new IllegalStateException("Already initialised");
        instance = new NMSMenuPacketListener(plugin);
    }

    public static NMSMenuPacketListener get() {
        if (instance == null) throw new IllegalStateException("Not initialised");
        return instance;
    }

    /** Call on plugin disable to clean up all injected handlers. */
    public void shutdown() {
        Bukkit.getOnlinePlayers().forEach(this::eject);
        instance = null;
    }

    // ──────────────────────────────────────────────────────────────
    // Public API
    // ──────────────────────────────────────────────────────────────

    /**
     * Block an outgoing packet type from reaching the player.
     * Useful to suppress vanilla inventory updates while a custom GUI is open.
     */
    public void discardOutgoing(Player player, Class<? extends Packet<ClientGamePacketListener>> type) {
        handler(player).discardRules.add(type);
    }

    public void stopDiscarding(Player player, Class<? extends Packet<ClientGamePacketListener>> type) {
        handler(player).discardRules.remove(type);
    }

    /**
     * Send a packet directly to the player, bypassing normal server logic.
     */
    public void sendPacket(Player player, Packet<? super ClientGamePacketListener> packet) {
        handler(player).sendPacket(packet);
    }

    /**
     * Send multiple packets as a single bundle.
     */
    public void sendPackets(Player player, List<Packet<? super ClientGamePacketListener>> packets) {
        if (packets.isEmpty()) return;
        sendPacket(player, new ClientboundBundlePacket(packets));
    }

    /**
     * Redirect an incoming packet type into a queue instead of letting it reach the server.
     * Use this to intercept inventory clicks, slot changes, etc.
     */
    public <T extends Packet<? super ServerGamePacketListener>> void redirectIncoming(
            Player player,
            Class<? extends T> type,
            PacketQueue<Packet<? super ServerGamePacketListener>> queue) {
        handler(player).redirections.put(type, queue);
    }

    public void stopRedirecting(Player player, Class<? extends Packet<ServerGamePacketListener>> type) {
        handler(player).redirections.remove(type);
    }

    /**
     * Listen to an incoming packet type WITHOUT blocking it.
     * The packet still reaches the server; a copy lands in your queue.
     */
    @SuppressWarnings("unchecked")
    public <T extends Packet<? super ServerGamePacketListener>> void listenIncoming(
            Player player,
            Class<? extends T> type,
            PacketQueue<Packet<? super ServerGamePacketListener>> queue) {
        handler(player).listeners.put(type, queue);
    }

    public void stopListening(Player player, Class<? extends Packet<? super ServerGamePacketListener>> type) {
        handler(player).listeners.remove(type);
    }

    // ──────────────────────────────────────────────────────────────
    // Events
    // ──────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.LOWEST)
    private void onJoin(PlayerJoinEvent e) {
        inject(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onQuit(PlayerQuitEvent e) {
        handlers.remove(e.getPlayer().getUniqueId());
    }

    // ──────────────────────────────────────────────────────────────
    // Internals
    // ──────────────────────────────────────────────────────────────

    private void inject(Player player) {
        if (handlers.containsKey(player.getUniqueId())) return;
        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        MenuPacketHandler h = new MenuPacketHandler(channel);
        handlers.put(player.getUniqueId(), h);
        channel.pipeline().addBefore(MC_HANDLER, MENU_HANDLER, h);
    }

    private void eject(Player player) {
        handlers.remove(player.getUniqueId());
        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        try {
            channel.pipeline().remove(MENU_HANDLER);
        } catch (NoSuchElementException ignored) {
        }
    }

    private MenuPacketHandler handler(Player player) {
        MenuPacketHandler h = handlers.get(player.getUniqueId());
        if (h == null) throw new IllegalStateException("No handler for player: " + player.getName());
        return h;
    }

    // ──────────────────────────────────────────────────────────────
    // Inner handler
    // ──────────────────────────────────────────────────────────────

    private static class MenuPacketHandler extends ChannelDuplexHandler {

        private final Set<Class<? extends Packet<ClientGamePacketListener>>> discardRules = Collections.newSetFromMap(new ConcurrentHashMap<>());

        private final Map<Class<? extends Packet<? super ServerGamePacketListener>>,
                PacketQueue<Packet<? super ServerGamePacketListener>>> redirections = new ConcurrentHashMap<>();

        private final Map<Class<? extends Packet<? super ServerGamePacketListener>>,
                PacketQueue<Packet<? super ServerGamePacketListener>>> listeners = new ConcurrentHashMap<>();

        private final Channel channel;

        MenuPacketHandler(Channel channel) {
            this.channel = channel;
        }

        void sendPacket(Packet<? super ClientGamePacketListener> packet) {
            if (!channel.eventLoop().inEventLoop()) {
                channel.eventLoop().execute(() -> sendPacket(packet));
                return;
            }

            channel.writeAndFlush(packet, new ForceChannelPromise(channel.newPromise().channel()));
        }


        @SuppressWarnings("unchecked")
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (!(msg instanceof Packet<?>) || promise instanceof ForceChannelPromise) {
                ctx.write(msg, promise);
                return;
            }

            if (msg instanceof ClientboundBundlePacket bundle) {
                var kept = StreamSupport.stream(bundle.subPackets().spliterator(), false)
                        .<Packet<? super ClientGamePacketListener>>map(p -> filterOutgoing((Packet<ClientGamePacketListener>) p))
                        .filter(Objects::nonNull)
                        .toList();

                if (kept.isEmpty()) promise.setSuccess();
                else ctx.write(new ClientboundBundlePacket(kept), promise);
            } else {
                var out = filterOutgoing((Packet<ClientGamePacketListener>) msg);
                if (out == null) promise.setSuccess();
                else ctx.write(out, promise);
            }
        }

        private @Nullable <P extends Packet<? super ClientGamePacketListener>> P filterOutgoing(P packet) {
            return discardRules.contains(packet.getClass()) ? null : packet;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (!(msg instanceof Packet<?> packet)) {
                super.channelRead(ctx, msg);
                return;
            }

            @SuppressWarnings("unchecked")
            var p = (Packet<? super ServerGamePacketListener>) packet;

            var listener = listeners.get(packet.getClass());
            if (listener != null) listener.offer(p);

            var redirect = redirections.get(packet.getClass());
            if (redirect != null) redirect.offer(p);
            else super.channelRead(ctx, packet);
        }
    }
}