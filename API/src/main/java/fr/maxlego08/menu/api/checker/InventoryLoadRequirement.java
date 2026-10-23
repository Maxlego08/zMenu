package fr.maxlego08.menu.api.checker;

import fr.maxlego08.menu.api.Inventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class InventoryLoadRequirement {

    private final Plugin plugin;
    private final YamlConfiguration configuration;
    private final Class<? extends Inventory> classz;
    private final File file;
    private final Map<InventoryRequirementType, List<String>> requirements = new HashMap<>();

    public InventoryLoadRequirement(Plugin plugin, YamlConfiguration configuration, Class<? extends Inventory> classz, File file) {
        this.plugin = plugin;
        this.configuration = configuration;
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

    @Contract(pure = true)
    @NotNull
    public YamlConfiguration getConfiguration() {
        return this.configuration;
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
