package fr.maxlego08.menu.mechanics.itemjoin;

import fr.maxlego08.menu.api.mechanic.Mechanic;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

public class ItemJoinMechanic extends Mechanic<ItemJoinMechanicFactory> {
    private final boolean grantOnFirstJoin;
    private final List<String> firstWorldJoinWorlds;
    private final boolean preventInventoryChanges;
    private final OptionalInt fixedSlot;

    public ItemJoinMechanic(String itemId, ItemJoinMechanicFactory itemJoinMechanicFactory, ConfigurationSection mechanicSection) {
        super(itemId, itemJoinMechanicFactory, mechanicSection);
        this.grantOnFirstJoin = mechanicSection.getBoolean("give-first-join", false);
        if (mechanicSection.isList("give-first-world-join")) {
            this.firstWorldJoinWorlds = mechanicSection.getStringList("give-first-world-join");
        } else if (mechanicSection.isString("give-first-world-join")) {
            this.firstWorldJoinWorlds = Collections.singletonList(mechanicSection.getString("give-first-world-join"));
        } else {
            this.firstWorldJoinWorlds = Collections.emptyList();
        }
        this.preventInventoryChanges = mechanicSection.getBoolean("prevent-inventory-modification", true);
        int slot = mechanicSection.getInt("fixed-slot", -1);
        this.fixedSlot = slot >= 0 && slot <= 36 ? OptionalInt.of(slot) : OptionalInt.empty();
    }

    public boolean shouldGrantOnFirstJoin() {
        return this.grantOnFirstJoin;
    }

    public List<String> getFirstWorldJoinWorlds() {
        return this.firstWorldJoinWorlds;
    }

    public boolean hasFirstWorldJoinWorlds() {
        return !this.firstWorldJoinWorlds.isEmpty();
    }

    public boolean preventsInventoryChanges() {
        return this.preventInventoryChanges;
    }

    public OptionalInt getFixedSlot() {
        return this.fixedSlot;
    }
}
