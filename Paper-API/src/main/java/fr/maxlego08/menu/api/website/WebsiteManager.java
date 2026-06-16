package fr.maxlego08.menu.api.website;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for managing integration with an external website or marketplace API.
 * Provides login/logout and marketplace actions for users. (Currently under development, do not use in production.)
 */
public interface WebsiteManager {

    /**
     * Allows you to connect to the site
     *
     * @param sender Command sender
     * @param token unique token
     */
    void login(@NotNull CommandSender sender, @Nullable String token);

    /**
     * Disconnect to the site
     *
     * @param sender Command sender
     */
    void disconnect(@NotNull CommandSender sender);

    void openMarketplace(@NotNull Player player);

    void downloadFromUrl(@NotNull CommandSender sender,@NotNull String url, boolean force);
}
