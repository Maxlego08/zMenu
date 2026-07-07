package fr.maxlego08.menu.api.website;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface WebsiteManager {

    void onEnable();

    void onDisable();

    boolean isLinked();

    void startDeviceFlow(CommandSender sender);

    void connect(CommandSender sender);

    void forceUnlink(CommandSender sender);

    String getApiUrl();

    void downloadFromUrl(@NotNull CommandSender sender, @NotNull String url, boolean force);
}
