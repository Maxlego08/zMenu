package fr.maxlego08.menu.zcore.utils.meta;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public class ClassicMeta extends ZUtils implements MetaUpdater {
    @Override
    public void updateDisplayName(ItemMeta itemMeta, String text, Player player) {
        itemMeta.setDisplayName(papi(color(text), player));
    }

    @Override
    public void updateLore(ItemMeta itemMeta, List<String> lore, Player player) {
        itemMeta.setLore(papi(color(lore), player));
    }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }
}
