package fr.maxlego08.menu.api.website;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Interface representing the manager of the website integration.
 */
public interface WebsiteManager {

    /**
     * Called when the plugin is enabled.
     */
    void onEnable();

    /**
     * Called when the plugin is disabled.
     */
    void onDisable();

    /**
     * Checks if the server is linked to the website.
     *
     * @return true if linked, false otherwise
     */
    boolean isLinked();

    /**
     * Starts the device flow to authenticate the server with the website.
     *
     * @param sender the sender of the command
     */
    void startDeviceFlow(CommandSender sender);

    /**
     * Connects the server to the website.
     *
     * @param sender the sender of the command
     */
    void connect(CommandSender sender);

    /**
     * Forces the unlinking of the server from the website.
     *
     * @param sender the sender of the command
     */
    void forceUnlink(CommandSender sender);

    /**
     * Gets the URL of the website's API.
     *
     * @return the URL of the website's API
     */
    String getApiUrl();

    /**
     * Downloads a file from a URL.
     *
     * @param sender the sender of the command
     * @param url    the URL of the file to download
     * @param force  whether to force the download even if it already exists
     */
    void downloadFromUrl(@NotNull CommandSender sender, @NotNull String url, boolean force);
}
