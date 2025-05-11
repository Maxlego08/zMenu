package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.RefreshRequirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.List;

public class ZRefreshRequirement implements RefreshRequirement {

    private final List<Permissible> enablePermissibles;
    private final List<Permissible> permissibles;
    private final boolean isTask;
    private final boolean isRefreshLore;
    private final boolean isRefreshName;
    private final boolean isRefreshButton;
    private final int updateInterval;

    public ZRefreshRequirement(List<Permissible> enablePermissibles, List<Permissible> permissibles, boolean isTask, boolean isRefreshLore, boolean isRefreshName, boolean isRefreshButton, int updateInterval) {
        this.enablePermissibles = enablePermissibles;
        this.permissibles = permissibles;
        this.isTask = isTask;
        this.isRefreshLore = isRefreshLore;
        this.isRefreshName = isRefreshName;
        this.isRefreshButton = isRefreshButton;
        this.updateInterval = updateInterval;
    }

    @Override
    public List<Permissible> getRequirements() {
        return this.permissibles;
    }

    @Override
    public List<Permissible> getEnableRequirements() {
        return this.enablePermissibles;
    }

    @Override
    public boolean isTask() {
        return isTask;
    }

    @Override
    public boolean isRefreshLore() {
        return isRefreshLore;
    }

    @Override
    public boolean isRefreshName() {
        return isRefreshName;
    }

    @Override
    public boolean isRefreshButton() {
        return isRefreshButton;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean needRefresh(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        return this.enablePermissibles.stream().allMatch(permissible -> permissible.hasPermission(player, button, inventory, placeholders));
    }

    @Override
    public boolean canRefresh(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        return this.permissibles.stream().allMatch(permissible -> permissible.hasPermission(player, button, inventory, placeholders));
    }
}
