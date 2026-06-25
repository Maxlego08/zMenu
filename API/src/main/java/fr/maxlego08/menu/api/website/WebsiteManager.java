package fr.maxlego08.menu.api.website;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for managing integration with an external website or marketplace API.
 * Provides login/logout and marketplace actions for users. (Currently under development, do not use in production.)
 */
public interface WebsiteManager {

    void openMarketplace(@NotNull Player player);

    void downloadFromUrl(@NotNull CommandSender sender, @NotNull String url, boolean force);
}
