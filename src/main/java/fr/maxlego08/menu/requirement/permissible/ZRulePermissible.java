package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.ItemSource;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.permissible.RulePermissible;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.rules.ZRuleContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ZRulePermissible extends RulePermissible {

    private final Rule rule;
    private final ItemSource itemSource;
    private final int amount;

    public ZRulePermissible(Rule rule, ItemSource itemSource, int amount, List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
        this.rule = rule;
        this.itemSource = itemSource;
        this.amount = amount;
    }

    @Override
    public boolean hasPermission(@NonNull Player player, Button button, @NonNull InventoryEngine inventoryEngine, @NonNull Placeholders placeholders) {
        if (this.itemSource == ItemSource.INVENTORY) {
            return this.checkInventory(player);
        }
        return this.checkSingleSlot(player);
    }

    private boolean checkSingleSlot(Player player) {
        ItemStack itemStack = this.getItemStack(player);
        return itemStack != null && itemStack.getType() != Material.AIR && this.rule.matches(new ZRuleContext(itemStack)) && itemStack.getAmount() >= this.amount;
    }

    private boolean checkInventory(Player player) {
        int count = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR && this.rule.matches(new ZRuleContext(itemStack))) {
                count += itemStack.getAmount();
                if (count >= this.amount) return true;
            }
        }
        return false;
    }

    private ItemStack getItemStack(Player player) {
        return switch (this.itemSource) {
            case HAND -> player.getInventory().getItemInMainHand();
            case OFF_HAND -> player.getInventory().getItemInOffHand();
            case HELMET -> player.getInventory().getHelmet();
            case CHESTPLATE -> player.getInventory().getChestplate();
            case LEGGINGS -> player.getInventory().getLeggings();
            case BOOTS -> player.getInventory().getBoots();
            default -> null;
        };
    }

    @Override
    public boolean isValid() {
        return this.rule != null;
    }

    @Override
    public @NonNull Rule getRule() {
        return this.rule;
    }

    @Override
    public @NonNull ItemSource getItemSource() {
        return this.itemSource;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }
}
