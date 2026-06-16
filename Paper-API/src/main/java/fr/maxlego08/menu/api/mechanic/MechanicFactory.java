package fr.maxlego08.menu.api.mechanic;

import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class MechanicFactory<T extends Mechanic<?>> {
    private final MenuPlugin plugin;
    private final ItemManager itemManager;
    private final String mechanicId;
    private final Map<String, T> mechanicByItemId = new HashMap<>();

    public MechanicFactory(@NotNull MenuPlugin plugin,@NotNull String mechanicId) {
        this.plugin = plugin;
        this.mechanicId = mechanicId;
        this.itemManager = plugin.getItemManager();
    }

    @NotNull
    public abstract T parse(final MenuPlugin plugin, final String itemId, final ConfigurationSection mechanicSection, YamlConfiguration configurationFile, File file, String path);

    @Contract(pure = true)
    public @Nullable T getMechanic(@NotNull String itemId){
        return this.mechanicByItemId.get(itemId);
    }

    public void addToImplemented(@NotNull T mechanic) {
        this.mechanicByItemId.put(mechanic.getItemId(), mechanic);
    }

    @Contract(pure = true)
    public boolean isNotImplementedIn(@Nullable ItemStack itemStack) {
        Optional<String> itemId = this.itemManager.getItemId(itemStack);
        return itemId.filter(string -> !this.mechanicByItemId.containsKey(string)).isPresent();
    }

    @Contract(pure = true)
    public boolean isNotImplementedIn(@NotNull String itemID) {
        return !this.mechanicByItemId.containsKey(itemID);
    }

    @Contract(pure = true)
    @NotNull
    public String getMechanicId() {
        return this.mechanicId;
    }

    @Contract
    @NotNull
    public MenuPlugin getPlugin() {
        return this.plugin;
    }

    @NotNull
    public Set<Map.Entry<String, T>> getAllMechanics(){
        return this.mechanicByItemId.entrySet();
    }

    public void clearMechanics() {
        this.mechanicByItemId.clear();
    }
}
