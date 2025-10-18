package fr.maxlego08.menu.api.mechanic;

import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class MechanicFactory {
    private final MenuPlugin plugin;
    private final ItemManager itemManager;
    private final String mechanicId;
    private final Map<String, Mechanic> mechanicByItemId = new HashMap<>();

    public MechanicFactory(MenuPlugin plugin, String mechanicId) {
        this.plugin = plugin;
        this.mechanicId = mechanicId;
        this.itemManager = plugin.getItemManager();
    }

    public abstract Mechanic parse(final MenuPlugin plugin, final String itemId, final ConfigurationSection mechanicSection, YamlConfiguration configurationFile, File file, String path);

    public Mechanic getMechanic(String itemId){
        return mechanicByItemId.get(itemId);
    };

    public void addToImplemented(Mechanic mechanic) {
        mechanicByItemId.put(mechanic.getItemId(), mechanic);
    }

    public boolean isNotImplementedIn(ItemStack itemStack) {
        Optional<String> itemId = itemManager.getItemId(itemStack);
        return itemId.filter(string -> !mechanicByItemId.containsKey(string)).isPresent();
    }

    public boolean isNotImplementedIn(String itemID) {
        return !mechanicByItemId.containsKey(itemID);
    }

    public String getMechanicId() {
        return mechanicId;
    }

    public Set<Map.Entry<String, Mechanic>> getAllMechanics(){
        return mechanicByItemId.entrySet();
    }

    public void clearMechanics() {
        mechanicByItemId.clear();
    }
}
