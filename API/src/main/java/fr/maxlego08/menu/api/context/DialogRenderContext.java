package fr.maxlego08.menu.api.context;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class DialogRenderContext<T, I extends Inventory, M extends MetaUpdater, P extends Plugin> {
    private final List<T> content;
    private final Player player;
    private final I inventory;
    private final M metaUpdater;
    private final Placeholders placeholders;
    private final P plugin;

    public DialogRenderContext(List<T> content, Player player, I inventory, M metaUpdater, Placeholders placeholders, P plugin) {
        this.content = content;
        this.player = player;
        this.inventory = inventory;
        this.metaUpdater = metaUpdater;
        this.placeholders = placeholders;
        this.plugin = plugin;
    }

    public List<T> getContent() {
        return this.content;
    }

    public Player getPlayer() {
        return this.player;
    }

    public I getInventory() {
        return this.inventory;
    }

    public M getMetaUpdater() {
        return this.metaUpdater;
    }

    public Placeholders getPlaceholders() {
        return this.placeholders;
    }

    public P getPlugin() {
        return this.plugin;
    }
}
