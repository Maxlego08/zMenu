package fr.maxlego08.menu.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DistantPlaceholder extends PlaceholderExpansion {

    private final LocalPlaceholder placeholder;

    public DistantPlaceholder(LocalPlaceholder placeholder) {
        super();
        this.placeholder = placeholder;
    }

    @Override
    public @NotNull String getAuthor() {
        return this.placeholder.getPlugin().getDescription().getAuthors().getFirst();
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.placeholder.getPrefix();
    }

    @Override
    public @NotNull String getVersion() {
        return this.placeholder.getPlugin().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        return this.placeholder.onRequest(offlinePlayer, params);
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return this.placeholder.onRequest(player, params);
    }

}
