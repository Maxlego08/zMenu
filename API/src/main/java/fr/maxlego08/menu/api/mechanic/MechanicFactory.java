package fr.maxlego08.menu.api.mechanic;

import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class MechanicFactory<T extends Mechanic> {
    private final MenuPlugin plugin;
    private final ItemManager itemManager;
    private final String mechanicId;
    private final Map<String, T> mechanicByItemId = new HashMap<>();

    public MechanicFactory(MenuPlugin plugin, String mechanicId) {
        this.plugin = plugin;
        this.mechanicId = mechanicId;
        this.itemManager = plugin.getItemManager();
    }

    public abstract T parse(final MenuPlugin plugin, final String itemId, final ConfigurationSection mechanicSection, YamlConfiguration configurationFile, File file, String path);

    public @Nullable T getMechanic(String itemId){
        return mechanicByItemId.get(itemId);
    }

    public void addToImplemented(T mechanic) {
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

    public Set<Map.Entry<String, T>> getAllMechanics(){
        return mechanicByItemId.entrySet();
    }

    public void clearMechanics() {
        mechanicByItemId.clear();
    }
}
