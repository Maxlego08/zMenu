package fr.maxlego08.menu.dupe;

import fr.maxlego08.menu.api.dupe.DupeItem;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class DupeListener implements Listener {

    private final ZScheduler schedule;
    private final DupeManager dupeManager;

    public DupeListener(ZScheduler schedule, DupeManager dupeManager) {
        this.schedule = schedule;
        this.dupeManager = dupeManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {

        if (event.isCancelled()) return;

        try {
            ItemStack itemStack = event.getItem();
            if (itemStack != null && this.dupeManager.isDupeItem(itemStack)) {
                event.setCancelled(true);
                event.getPlayer().getInventory().setItem(event.getHand(), new ItemStack(Material.AIR));
                sendInformation(new DupeItem(itemStack, event.getPlayer()));
            }
        } catch (Exception exception) {
            ItemStack itemStack = event.getPlayer().getItemInHand();
            if (itemStack != null && this.dupeManager.isDupeItem(itemStack)) {
                event.setCancelled(true);
                event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                sendInformation(new DupeItem(itemStack, event.getPlayer()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent event) {

        if (event.isCancelled()) return;

        Item item = event.getItemDrop();
        ItemStack itemStack = item.getItemStack();
        if (this.dupeManager.isDupeItem(itemStack)) {
            item.remove();
            sendInformation(new DupeItem(itemStack, event.getPlayer()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(BlockPlaceEvent event) {

        if (event.isCancelled()) return;

        try {
            ItemStack itemStack = event.getItemInHand();
            if (this.dupeManager.isDupeItem(itemStack)) {
                event.getPlayer().getInventory().setItem(event.getHand(), new ItemStack(Material.AIR));
                event.setCancelled(true);
                sendInformation(new DupeItem(itemStack, event.getPlayer()));
            }
        } catch (Exception ignored) {

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerPickupItemEvent event) {

        if (event.isCancelled()) return;

        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        if (this.dupeManager.isDupeItem(itemStack)) {
            item.remove();
            event.setCancelled(true);
            sendInformation(new DupeItem(itemStack, event.getPlayer()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.isCancelled()) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack != null) {
            if (this.dupeManager.isDupeItem(itemStack)) {
                event.setCurrentItem(new ItemStack(Material.AIR));
                event.setCancelled(true);
                sendInformation(new DupeItem(itemStack, player));
            }
        }

        itemStack = event.getCursor();
        if (itemStack != null) {
            if (this.dupeManager.isDupeItem(itemStack)) {
                event.setCursor(new ItemStack(Material.AIR));
                event.setCancelled(true);
                sendInformation(new DupeItem(itemStack, player));
            }
        }

    }

    private void sendInformation(DupeItem dupeItem) {

        // Rework with new discord webhook class

        /*DiscordWebhook discordWebhook = new DiscordWebhook(Config.antiDupeDiscordWebhookUrl);
        discordWebhook.setContent(Config.antiDupeMessage);

        Logger.info(discordWebhook.replaceString(Config.antiDupeMessage, dupeItem), Logger.LogType.WARNING);
        if (Config.enableAntiDupeDiscordNotification) {
            schedule.runTaskAsynchronously(() -> {
                try {
                    discordWebhook.execute(dupeItem);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
        }*/
    }

}
