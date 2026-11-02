package fr.maxlego08.menu.api.storage.spi;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

/**
 * What zMenu hands a {@link StorageProvider} when it opens.
 *
 * @param plugin        zMenu
 * @param configuration zMenu's {@code config.yml}; a provider reads its own settings from it
 * @param dataFolder    the folder a file-based provider writes into
 * @param logger        the logger to report problems with
 * @param serverId      the {@code server-id} of this server; saved inventories belong to it, and
 *                      events it publishes carry it
 */
@ApiStatus.Experimental
public record StorageContext(@NotNull Plugin plugin, @NotNull ConfigurationSection configuration, @NotNull File dataFolder,
                             @NotNull Logger logger, @NotNull String serverId) {
}
