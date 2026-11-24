package fr.maxlego08.menu.hooks.itemsadder;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoListener;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.text.TextService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Font images are replaced when a text is compiled, and ItemsAdder loads them after zMenu has loaded its menus: once
 * ItemsAdder has its data, the texts are compiled again so their {@code :name:} become the glyphs.
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
            TextService.get().flow().clearCache();
            this.plugin.getMetaUpdater().clearCache();
            this.plugin.getPatternManager().loadActionsPatterns();
            this.plugin.getPatternManager().loadPatterns();
            this.plugin.getInventoryManager().loadInventories();
        });
    }
}
