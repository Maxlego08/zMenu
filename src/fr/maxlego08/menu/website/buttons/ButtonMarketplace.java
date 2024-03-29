package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.website.Resource;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import fr.maxlego08.menu.zcore.utils.inventory.Pagination;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ButtonMarketplace extends ZButton {

    private final MenuPlugin plugin;

    public ButtonMarketplace(Plugin plugin) {
        this.plugin = (MenuPlugin) plugin;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {

        List<Resource> resources = plugin.getWebsiteManager().getResources();
        Pagination<Resource> pagination = new Pagination<>();
        AtomicInteger slot = new AtomicInteger();
        pagination.paginate(resources, 45, inventory.getPage()).forEach(resource -> {

            Placeholders placeholders = resource.getPlaceholders();

            inventory.addItem(slot.getAndIncrement(), this.getItemStack().build(player, false, placeholders)).setLeftClick(event -> {
                player.closeInventory();

                OpenLink openLink = new ZOpenLink(ClickEvent.Action.OPEN_URL, "§7" + resource.getName(), resource.getLink(), resource.getName(), Collections.singletonList("§7Click here"));
                openLink.send(player, Collections.singletonList("§fResource§8: §7" + resource.getName()));

            }).setRightClick(event -> {
                System.out.println("TODO");
            });
        });

    }
}
