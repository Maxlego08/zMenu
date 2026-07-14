package fr.maxlego08.menu.mechanics.onclick;

import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.mechanic.MechanicListener;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.mechanics.onclick.OnClickMechanic.ClickTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OnClickMechanicListener extends MechanicListener {

    private final OnClickMechanicFactory factory;
    private final ItemManager itemManager;
    private final MenuPlugin plugin;
    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    public OnClickMechanicListener(OnClickMechanicFactory factory, MenuPlugin plugin) {
        this.factory = factory;
        this.itemManager = plugin.getItemManager();
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == org.bukkit.event.block.Action.PHYSICAL) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null) return;

        Optional<String> optItemId = this.itemManager.getItemId(item);
        if (optItemId.isEmpty()) return;

        String itemId = optItemId.get();
        OnClickMechanic mechanic = this.factory.getMechanic(itemId);
        if (mechanic == null) return;

        ClickType clickType = mapActionToClickType(event.getAction(), player.isSneaking());
        if (!mechanic.getClickTypes().contains(clickType)) return;

        if (!isValidTarget(event.getAction(), mechanic.getClickTarget())) return;

        if (isOnCooldown(itemId, player.getUniqueId(), mechanic.getCooldown())) return;

        InventoryEngine inventory = this.plugin.getInventoryManager().getFakeInventory();
        Placeholders placeholders = new Placeholders();

        for (Requirement requirement : mechanic.getClickRequirements()) {
            requirement.execute(player, null, inventory, placeholders);
        }

        for (Action action : mechanic.getActions()) {
            action.preExecute(player, null, inventory, placeholders);
        }

        if (mechanic.shouldCancelEvent()) {
            event.setCancelled(true);
        }

        setCooldown(itemId, player.getUniqueId(), mechanic.getCooldown());
    }

    private boolean isValidTarget(org.bukkit.event.block.Action action, ClickTarget target) {
        if (target == ClickTarget.BOTH) return true;
        boolean isAir = action == org.bukkit.event.block.Action.LEFT_CLICK_AIR
                || action == org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
        return (target == ClickTarget.AIR && isAir) || (target == ClickTarget.BLOCK && !isAir);
    }

    private ClickType mapActionToClickType(org.bukkit.event.block.Action action, boolean sneaking) {
        boolean isRight = action == org.bukkit.event.block.Action.RIGHT_CLICK_AIR
                || action == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
        if (sneaking) {
            return isRight ? ClickType.SHIFT_RIGHT : ClickType.SHIFT_LEFT;
        }
        return isRight ? ClickType.RIGHT : ClickType.LEFT;
    }

    private boolean isOnCooldown(String itemId, UUID playerUuid, int cooldownSeconds) {
        if (cooldownSeconds <= 0) return false;
        Map<UUID, Long> playerCooldowns = this.cooldowns.get(itemId);
        if (playerCooldowns == null) return false;
        Long expiry = playerCooldowns.get(playerUuid);
        return expiry != null && System.currentTimeMillis() < expiry;
    }

    private void setCooldown(String itemId, UUID playerUuid, int cooldownSeconds) {
        if (cooldownSeconds <= 0) return;
        long expiry = System.currentTimeMillis() + (cooldownSeconds * 1000L);
        this.cooldowns.computeIfAbsent(itemId, k -> new HashMap<>()).put(playerUuid, expiry);
    }
}
