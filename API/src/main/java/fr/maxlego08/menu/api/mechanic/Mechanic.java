package fr.maxlego08.menu.api.mechanic;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Mechanic<T extends MechanicFactory<?>> {
    private final ConfigurationSection mechanicSection;
    private final T mechanicFactory;
    private final String itemId;

    public Mechanic(final String itemId, final T mechanicFactory, final ConfigurationSection mechanicSection) {
        this.mechanicFactory = mechanicFactory;
        this.mechanicSection = mechanicSection;
        this.itemId = itemId;
    }

    public ConfigurationSection getMechanicSection() {
        return mechanicSection;
    }

    public T getMechanicFactory() {
        return mechanicFactory;
    }

    public String getItemId() {
        return itemId;
    }
}
