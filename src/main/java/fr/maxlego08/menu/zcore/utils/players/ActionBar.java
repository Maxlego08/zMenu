package fr.maxlego08.menu.zcore.utils.players;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBar {

    public static void sendActionBar(Player player, String message) {

        if (!player.isOnline()) {
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(TextComponent.fromLegacyText(message)));
    }
}
