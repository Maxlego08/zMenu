package fr.maxlego08.menu.api.website;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * ToDO
 * In dev, dont use that
 */
public interface WebsiteManager {

    /**
     * Allows you to connect to the site
     *
     * @param sender Command sender
     * @param token unique token
     */
    void login(CommandSender sender, String token);

    /**
     * Disconnect to the site
     *
     * @param sender Command sender
     */
    void disconnect(CommandSender sender);

    void openMarketplace(Player player);
}
