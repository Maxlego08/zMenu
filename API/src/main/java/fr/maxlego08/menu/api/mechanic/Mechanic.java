package fr.maxlego08.menu.api.mechanic;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class Mechanic<T extends MechanicFactory<?>> {
    private final ConfigurationSection mechanicSection;
    private final T mechanicFactory;
    private final String itemId;

    public Mechanic(@NotNull final String itemId, @NotNull final T mechanicFactory,@NotNull final ConfigurationSection mechanicSection) {
        this.mechanicFactory = mechanicFactory;
        this.mechanicSection = mechanicSection;
        this.itemId = itemId;
    }

    @Contract(pure = true)
    @NotNull
    public ConfigurationSection getMechanicSection() {
        return this.mechanicSection;
    }

    @Contract(pure = true)
    @NotNull
    public T getMechanicFactory() {
        return this.mechanicFactory;
    }

    @Contract(pure = true)
    @NotNull
    public String getItemId() {
        return this.itemId;
    }
}
