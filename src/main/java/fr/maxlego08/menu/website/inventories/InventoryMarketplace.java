package fr.maxlego08.menu.website.inventories;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.website.Resource;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;

public class InventoryMarketplace extends ZInventory {

    private final MenuPlugin plugin;

    /**
     * @param plugin   The plugin where the inventory comes from
     * @param name     Inventory name
     * @param fileName Inventory file name
     * @param size     Inventory size
     * @param buttons  List of {@link Button}
     */
    public InventoryMarketplace(Plugin plugin, String name, String fileName, int size, List<Button> buttons) {
        super(plugin, name, fileName, size, buttons);
        this.plugin = (MenuPlugin) plugin;
    }

    @Override
    public int getMaxPage(Collection<Pattern> patterns, Player player, Object... objects) {
        List<Resource> resources = this.plugin.getWebsiteManager().getResources();
        return getMaxPage(resources, 45);
    }
}
