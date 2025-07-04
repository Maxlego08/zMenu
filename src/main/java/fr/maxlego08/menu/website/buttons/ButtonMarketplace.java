package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.website.Resource;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import fr.maxlego08.menu.api.engine.Pagination;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ButtonMarketplace extends Button {

    private final ZMenuPlugin plugin;

    public ButtonMarketplace(Plugin plugin) {
        this.plugin = (ZMenuPlugin) plugin;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {

        List<Resource> resources = plugin.getWebsiteManager().getResources();
        Pagination<Resource> pagination = new Pagination<>();
        AtomicInteger slot = new AtomicInteger();
        pagination.paginate(resources, 45, inventory.getPage()).forEach(resource -> {

            Placeholders placeholders = resource.getPlaceholders();

            inventory.addItem(slot.getAndIncrement(), this.getItemStack().build(player, false, placeholders)).setLeftClick(event -> {
                player.closeInventory();

                OpenLink openLink = new ZOpenLink(this.plugin, ClickEvent.Action.OPEN_URL, "§7" + resource.getName(), resource.getLink(), resource.getName(), Collections.singletonList("§7Click here"));
                openLink.send(player, Collections.singletonList("§fResource§8: §7" + resource.getName()));

            }).setRightClick(event -> System.out.println("TODO"));
        });

    }
}
