package fr.maxlego08.menu.zcore.utils.players;

import fr.maxlego08.menu.zcore.ZPlugin;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ActionBar {

    private static final double nmsVersion = NMSUtils.getNMSVersion();
    private static String nmsVersionAsString;
    private static boolean useOldMethods = false;
    private static boolean useChatComponent;
    private static Class<?> craftPlayerClass;
    private static Class<?> packetClass;
    private static Class<?> packetPlayOutChatClass;
    private static Class<?> chatComponentTextClass;
    private static Class<?> iChatBaseComponentClass;
    private static Class<?> chatMessageTypeClass;
    private static Class<?> chatSerializerClass;
    private static Method getHandleMethod;
    private static Field playerConnectionField;

    static {
        nmsVersionAsString = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersionAsString = nmsVersionAsString.substring(nmsVersionAsString.lastIndexOf(".") + 1);

        try {
            craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersionAsString + ".entity.CraftPlayer");
            packetPlayOutChatClass = Class.forName("net.minecraft.server." + nmsVersionAsString + ".PacketPlayOutChat");
            packetClass = Class.forName("net.minecraft.server." + nmsVersionAsString + ".Packet");
            iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsVersionAsString + ".IChatBaseComponent");

            getHandleMethod = craftPlayerClass.getMethod("getHandle");
            playerConnectionField = getHandleMethod.getReturnType().getField("playerConnection");

            chatComponentTextClass = Class.forName("net.minecraft.server." + nmsVersionAsString + ".ChatComponentText");

        } catch (Exception ignored) {
        }

        try {
            chatSerializerClass = Class.forName("net.minecraft.server." + nmsVersionAsString + ".ChatSerializer");
            chatMessageTypeClass = Class.forName("net.minecraft.server." + nmsVersionAsString + ".ChatMessageType");
            useChatComponent = true;
        } catch (Exception ignored) {
            useChatComponent = false;
        }
    }

    public static void sendActionBar(Player player, String message) {

        if (!player.isOnline()) {
            return;
        }

        if (nmsVersionAsString.equalsIgnoreCase("v1_8_R1") || nmsVersionAsString.startsWith("v1_7_")) {
            useOldMethods = true;
        }

        // Use new methods
        if (nmsVersion != 1.7 && nmsVersion != 1.8 && nmsVersion != 1.9) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(TextComponent.fromLegacyText(message)));

            return;
        }

        ZPlugin.service.execute(() -> {
            try {
                Object craftPlayer = craftPlayerClass.cast(player);
                Object packetContent;
                Object packet;
                if (useOldMethods) {
                    Method var20 = chatSerializerClass.getDeclaredMethod("a", String.class);
                    packet = iChatBaseComponentClass.cast(var20.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                    packetContent = packetPlayOutChatClass.getConstructor(new Class[]{iChatBaseComponentClass, Byte.TYPE})
                            .newInstance(packet, (byte) 2);
                } else {
                    if (useChatComponent) {

                        Object[] values = chatMessageTypeClass.getEnumConstants();
                        Object enumValue = null;

                        for (Object currentValue : values) {
                            if (currentValue.toString().equals("GAME_INFO")) {
                                enumValue = currentValue;
                            }
                        }

                        Object component = chatComponentTextClass.getConstructor(new Class[]{String.class})
                                .newInstance(message);
                        packetContent = packetPlayOutChatClass.getConstructor(new Class[]{iChatBaseComponentClass, chatSerializerClass})
                                .newInstance(component, enumValue);
                    } else {
                        packet = chatComponentTextClass.getConstructor(new Class[]{String.class})
                                .newInstance(message);
                        packetContent = packetPlayOutChatClass.getConstructor(new Class[]{iChatBaseComponentClass, Byte.TYPE})
                                .newInstance(packet, (byte) 2);
                    }
                }

                Object serverPlayer = getHandleMethod.invoke(craftPlayer);
                packet = playerConnectionField.get(serverPlayer);
                Method packetMethod = packet.getClass().getDeclaredMethod("sendPacket", packetClass);
                packetMethod.invoke(packet, packetContent);
            } catch (Exception message7) {
                message7.printStackTrace();
            }
        });
    }
}