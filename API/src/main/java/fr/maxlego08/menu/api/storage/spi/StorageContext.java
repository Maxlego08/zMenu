package fr.maxlego08.menu.api.storage.spi;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

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

    /**
     * @deprecated use {@link #section()}, zMenu reads its files with BoostedYAML.
     */
    @Override
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    public @NotNull ConfigurationSection configuration() {
        return this.configuration;
    }

    /**
     * zMenu's {@code config.yml}, as BoostedYAML reads it; a provider reads its own settings from it.
     * <p>BoostedYAML is loaded by zMenu: an addon that calls this method must also declare it.
     */
    public @NotNull Section section() {
        return YamlSections.toDocument(this.configuration);
    }
}
