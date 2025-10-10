package fr.maxlego08.menu.api.mechanic;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Mechanic {
    private final ConfigurationSection mechanicSection;
    private final MechanicFactory mechanicFactory;
    private final String itemId;

    public Mechanic(final String itemId, final MechanicFactory mechanicFactory, final ConfigurationSection mechanicSection) {
        this.mechanicFactory = mechanicFactory;
        this.mechanicSection = mechanicSection;
        this.itemId = itemId;
    }

    public ConfigurationSection getMechanicSection() {
        return mechanicSection;
    }

    public MechanicFactory getMechanicFactory() {
        return mechanicFactory;
    }

    public String getItemId() {
        return itemId;
    }
}
