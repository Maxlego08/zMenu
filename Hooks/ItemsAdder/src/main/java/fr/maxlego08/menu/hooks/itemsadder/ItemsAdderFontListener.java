package fr.maxlego08.menu.hooks.itemsadder;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoListener;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.text.TextService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Font images are replaced when a text is compiled, and ItemsAdder loads them after zMenu has compiled its texts: once
 * ItemsAdder has its data, every compiled text is compiled again on its next use so its {@code :name:} become the
 * glyphs. Nothing is loaded again: the templates are told by the text engine, and the items built from them are built
 * again since the engine's revision changed.
 */
@AutoListener
@RequiresPlugin("ItemsAdder")
public class ItemsAdderFontListener implements Listener {

    private final MenuPlugin plugin;

    public ItemsAdderFontListener(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLoadData(ItemsAdderLoadDataEvent event) {
        this.plugin.getScheduler().runNextTick(task -> {
            TextService.get().flow().recompile();
            this.plugin.getMetaUpdater().clearCache();
        });
    }
}
