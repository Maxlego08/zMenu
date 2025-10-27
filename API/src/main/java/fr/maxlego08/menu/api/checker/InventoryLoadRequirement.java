package fr.maxlego08.menu.api.checker;

import fr.maxlego08.menu.api.Inventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public File getFile() {
        return file;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public Class<? extends Inventory> getClassz() {
        return classz;
    }

    public void addRequirement(InventoryRequirementType inventoryRequirementType, String name) {
        this.requirements.get(inventoryRequirementType).add(name);
    }

    public void removeRequirement(InventoryRequirementType inventoryRequirementType, String name) {
        this.requirements.get(inventoryRequirementType).removeIf(e -> e.equalsIgnoreCase(name));
    }

    public boolean canLoad() {
        for (List<String> names : this.requirements.values()) {
            if (!names.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Map<InventoryRequirementType, List<String>> getRequirements() {
        return requirements;
    }


    public String getDisplayError() {
        StringBuilder sb = new StringBuilder();
        this.requirements.forEach((type, names) -> {
            if (!names.isEmpty()) {
                sb.append(type.name().toUpperCase()).append(" : ");
                sb.append(String.join(", ", names));
                sb.append("\n");
            }
        });
        return sb.toString();
    }

    @Override
    public String toString() {
        return "InventoryLoadRequirement{" +
                "plugin=" + plugin +
                ", configuration=" + configuration +
                ", classz=" + classz +
                ", file=" + file +
                ", requirements=" + requirements +
                '}';
    }
}
