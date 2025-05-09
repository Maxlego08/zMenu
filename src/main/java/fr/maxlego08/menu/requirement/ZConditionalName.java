package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.ConditionalName;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

import java.util.List;

public class ZConditionalName implements ConditionalName {

    private final String name;
    private final List<Permissible> permissibles;
    private final int priority;

    public ZConditionalName(String name, List<Permissible> permissibles, int priority) {
        this.name = name;
        this.permissibles = permissibles;
        this.priority = priority;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Permissible> getPermissibles() {
        return this.permissibles;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean hasPermission(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        return this.permissibles.stream().allMatch(permissible -> permissible.hasPermission(player, button, inventory, placeholders));
    }
}
