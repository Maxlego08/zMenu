package fr.maxlego08.menu.nms.v1_21_R1.network;

import com.mojang.authlib.GameProfile;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.dialog.canvas.CanvasManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.papermc.paper.network.ChannelInitializeListenerHolder;
import net.kyori.adventure.key.Key;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.UUID;

public final class ConfigurationResourcePackListener {

    private static final Key LISTENER_KEY = Key.key("zmenu", "configuration_resource_pack");
    private static final String MC_HANDLER = "packet_handler";
    private static final String HANDLER = "zmenu_configuration_resource_pack";
    private static final MethodHandle PROFILE_ID = findProfileId();

    private ConfigurationResourcePackListener() {
    }

    public static void register(MenuPlugin plugin) {
        if (ChannelInitializeListenerHolder.hasListener(LISTENER_KEY)) return;
        Handler handler = new Handler(plugin);
        ChannelInitializeListenerHolder.addListener(LISTENER_KEY, channel -> {
            ChannelPipeline pipeline = channel.pipeline();
            if (pipeline.get(HANDLER) != null) return;
            if (pipeline.get(MC_HANDLER) != null) pipeline.addBefore(MC_HANDLER, HANDLER, handler);
            else pipeline.addLast(HANDLER, handler);
        });
    }

    public static void unregister() {
        ChannelInitializeListenerHolder.removeListener(LISTENER_KEY);
    }

    private static MethodHandle findProfileId() {
        MethodHandles.Lookup lookup = MethodHandles.publicLookup();
        MethodType type = MethodType.methodType(UUID.class);
        for (String name : new String[]{"id", "getId"}) {
            try {
                return lookup.findVirtual(GameProfile.class, name, type);
            } catch (NoSuchMethodException | IllegalAccessException ignored) {
            }
        }
        return null;
    }

    private static UUID profileId(GameProfile profile) throws Throwable {
        if (PROFILE_ID == null || profile == null) return null;
        return (UUID) PROFILE_ID.invoke(profile);
    }

    @ChannelHandler.Sharable
    private static final class Handler extends ChannelInboundHandlerAdapter {

        private final MenuPlugin plugin;

        private Handler(MenuPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof ServerboundResourcePackPacket packet) {
                try {
                    this.report(ctx, packet);
                } catch (Throwable throwable) {
                    if (Configuration.enableDebug) {
                        Logger.info("[Canvas] Could not read a resource pack answer while joining: " + throwable, Logger.LogType.WARNING);
                    }
                }
            }
            super.channelRead(ctx, msg);
        }

        private void report(ChannelHandlerContext ctx, ServerboundResourcePackPacket packet) throws Throwable {
            CanvasManager canvasManager = this.plugin.getCanvasManager();
            if (canvasManager == null) return;

            if (!(ctx.pipeline().get(MC_HANDLER) instanceof Connection connection)) return;
            if (!(connection.getPacketListener() instanceof ServerConfigurationPacketListenerImpl listener)) return;

            UUID uniqueId = profileId(listener.getOwner());
            if (uniqueId == null) return;

            if (Configuration.enableDebug) {
                Logger.info("[Canvas] Resource pack " + packet.id() + " of " + uniqueId + " while joining: " + packet.action());
            }
            switch (packet.action()) {
                case SUCCESSFULLY_LOADED -> canvasManager.reportResourcePack(uniqueId, packet.id(), true);
                case DECLINED, FAILED_DOWNLOAD, INVALID_URL, FAILED_RELOAD, DISCARDED -> canvasManager.reportResourcePack(uniqueId, packet.id(), false);
                default -> {
                }
            }
        }
    }
}
