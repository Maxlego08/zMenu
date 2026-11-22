package fr.maxlego08.menu.api.mechanic;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@SuppressWarnings("unused")
public abstract class Mechanic<T extends MechanicFactory<?>> {
    private final ConfigurationSection mechanicSection;
    private final Section section;
    private final T mechanicFactory;
    private final String itemId;

    /**
     * @deprecated use {@link #Mechanic(String, MechanicFactory, Section)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    public Mechanic(@NotNull final String itemId, @NotNull final T mechanicFactory,@NotNull final ConfigurationSection mechanicSection) {
        this.mechanicFactory = mechanicFactory;
        this.mechanicSection = mechanicSection;
        this.section = YamlSections.toDocument(mechanicSection);
        this.itemId = itemId;
    }

    /**
     * {@link #getMechanicSection()} is then a Bukkit copy of the section.
     */
    public Mechanic(@NotNull final String itemId, @NotNull final T mechanicFactory, @NotNull final Section mechanicSection) {
        this.mechanicFactory = mechanicFactory;
        this.section = mechanicSection;
        this.mechanicSection = YamlSections.toBukkit(mechanicSection);
        this.itemId = itemId;
    }

    /**
     * @deprecated use {@link #getSection()}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    @Contract(pure = true)
    @NotNull
    public ConfigurationSection getMechanicSection() {
        return this.mechanicSection;
    }

    @Contract(pure = true)
    @NotNull
    public Section getSection() {
        return this.section;
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
