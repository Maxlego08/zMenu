package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultDepositAction extends Action {

    private final double amount;

    public VaultDepositAction(double amount) {
        this.amount = amount;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        getEconomy().depositPlayer(player, this.amount);
    }

    private Economy getEconomy() {
        RegisteredServiceProvider<Economy> registration = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        return registration.getProvider();
    }
}
