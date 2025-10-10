package fr.maxlego08.menu.mechanics.itemjoin;

import fr.maxlego08.menu.api.mechanic.Mechanic;
import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.OptionalInt;

public class ItemJoinMechanic extends Mechanic {
    private final boolean grantOnFirstJoin;
    private final boolean preventInventoryChanges;
    private final OptionalInt fixedSlot;

    public ItemJoinMechanic(String itemId, MechanicFactory mechanicFactory, ConfigurationSection mechanicSection) {
        super(itemId, mechanicFactory, mechanicSection);
        this.grantOnFirstJoin = mechanicSection.getBoolean("give-first-join", false);
        this.preventInventoryChanges = mechanicSection.getBoolean("prevent-inventory-modification", true);
        int slot = mechanicSection.getInt("fixed-slot", -1);
        this.fixedSlot = slot >= 0 && slot <= 36 ? OptionalInt.of(slot) : OptionalInt.empty();
    }

    public boolean shouldGrantOnFirstJoin() {
        return grantOnFirstJoin;
    }

    public boolean preventsInventoryChanges() {
        return preventInventoryChanges;
    }

    public OptionalInt getFixedSlot() {
        return fixedSlot;
    }
}
