package fr.maxlego08.menu.listener;

import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

@SuppressWarnings("deprecation")
public abstract class ListenerAdapter extends ZUtils {

    protected void onConnect(PlayerJoinEvent event, Player player) {
    }

    protected void onQuit(PlayerQuitEvent event, Player player) {
    }

    protected void onMove(PlayerMoveEvent event, Player player) {
    }

    protected void onInventoryClick(InventoryClickEvent event, Player player) {
    }

    protected void onInventoryClose(InventoryCloseEvent event, Player player) {
    }

    protected void onInventoryDrag(InventoryDragEvent event, Player player) {
    }

    protected void onBlockBreak(BlockBreakEvent event, Player player) {
    }

    protected void onBlockPlace(BlockPlaceEvent event, Player player) {
    }

    protected void onEntityDeath(EntityDeathEvent event, Entity entity) {
    }

    protected void onInteract(PlayerInteractEvent event, Player player) {
    }

    protected void onPlayerTalk(AsyncPlayerChatEvent event, String message) {
    }

    protected void onCraftItem(CraftItemEvent event) {
    }

    protected void onCommand(PlayerCommandPreprocessEvent event, Player player, String message) {
    }

    protected void onGamemodeChange(PlayerGameModeChangeEvent event, Player player) {
    }

    public void onDrop(PlayerDropItemEvent event, Player player) {
    }

    public void onPickUp(PlayerPickupItemEvent event, Player player) {
    }

    public void onMobSpawn(CreatureSpawnEvent event) {
    }

    public void onDamageByEntity(EntityDamageByEntityEvent event, DamageCause cause, double damage, LivingEntity damager,
                                 LivingEntity entity) {
    }

    public void onPlayerDamagaByPlayer(EntityDamageByEntityEvent event, DamageCause cause, double damage,
                                       Player damager, Player entity) {
    }

    public void onPlayerDamagaByArrow(EntityDamageByEntityEvent event, DamageCause cause, double damage,
                                      Projectile damager, Player entity) {
    }

    public void onItemisOnGround(PlayerDropItemEvent event, Player player, Item item, Location location) {
    }

    public void onItemMove(PlayerDropItemEvent event, Player player, Item item, Location location, Block block) {
    }

    public void onPlayerWalk(PlayerMoveEvent event, Player player, int i) {
    }
}
