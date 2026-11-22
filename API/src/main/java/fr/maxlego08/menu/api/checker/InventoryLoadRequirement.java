package fr.maxlego08.menu.api.checker;

import fr.maxlego08.menu.api.Inventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import dev.dejvokep.boostedyaml.YamlDocument;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class InventoryLoadRequirement {

    private final Plugin plugin;
    private final YamlConfiguration configuration;
    private final YamlDocument document;
    private final Class<? extends Inventory> classz;
    private final File file;
    private final Map<InventoryRequirementType, List<String>> requirements = new HashMap<>();

    /**
     * @deprecated use {@link #InventoryLoadRequirement(Plugin, YamlDocument, Class, File)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    public InventoryLoadRequirement(Plugin plugin, YamlConfiguration configuration, Class<? extends Inventory> classz, File file) {
        this(plugin, configuration, YamlSections.toDocument(configuration), classz, file);
    }

    /**
     * {@link #getConfiguration()} is then a Bukkit copy of the document.
     */
    public InventoryLoadRequirement(Plugin plugin, YamlDocument document, Class<? extends Inventory> classz, File file) {
        this(plugin, YamlSections.toBukkit(document), document, classz, file);
    }

    private InventoryLoadRequirement(Plugin plugin, YamlConfiguration configuration, YamlDocument document, Class<? extends Inventory> classz, File file) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.document = document;
        this.classz = classz;
        this.file = file;
        for (InventoryRequirementType value : InventoryRequirementType.values()) {
            this.requirements.put(value, new ArrayList<>());
        }
    }

    @Contract(pure = true)
    @NotNull
    public File getFile() {
        return this.file;
    }

    @Contract(pure = true)
    @NotNull
    public Plugin getPlugin() {
        return this.plugin;
    }

    /**
     * @deprecated use {@link #getDocument()}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    @Contract(pure = true)
    @NotNull
    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    @Contract(pure = true)
    @NotNull
    public YamlDocument getDocument() {
        return this.document;
    }

    @Contract(pure = true)
    @NotNull
    public Class<? extends Inventory> getClassz() {
        return this.classz;
    }

    public void addRequirement(@NotNull InventoryRequirementType inventoryRequirementType, @NotNull String name) {
        this.requirements.get(inventoryRequirementType).add(name);
    }

    public void removeRequirement(@NotNull InventoryRequirementType inventoryRequirementType, @NotNull String name) {
        String loaded = normalize(name);
        this.requirements.get(inventoryRequirementType).removeIf(e -> normalize(e).equals(loaded));
    }

    /**
     * Reads a name the way registries read keys, so that a menu waiting for {@code play animation}
     * loads once {@code play_animation} is registered.
     */
    private static String normalize(String name) {
        return name.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    }

    @Contract(pure = true)
    public boolean canLoad() {
        for (List<String> names : this.requirements.values()) {
            if (!names.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Contract(pure = true)
    @NotNull
    public Map<InventoryRequirementType, List<String>> getRequirements() {
        return this.requirements;
    }

    @Contract(pure = true)
    @NotNull
    public String getDisplayError() {
        StringBuilder sb = new StringBuilder();
        this.requirements.forEach((type, names) -> {
            if (!names.isEmpty()) {
                sb.append(type.name().toUpperCase(Locale.ROOT)).append(" : ");
                sb.append(String.join(", ", names));
                sb.append("\n");
            }
        });
        return sb.toString();
    }

    @Override
    @Contract(pure = true)
    @NotNull
    public String toString() {
        return "InventoryLoadRequirement{" +
                "plugin=" + this.plugin +
                ", configuration=" + this.configuration +
                ", classz=" + this.classz +
                ", file=" + this.file +
                ", requirements=" + this.requirements +
                '}';
    }
}
