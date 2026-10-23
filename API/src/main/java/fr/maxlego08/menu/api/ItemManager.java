package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import fr.maxlego08.menu.api.mechanic.MechanicListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Optional;
import java.util.Set;

public interface ItemManager {

    void loadAll();

    void loadCustomItems();

    void loadCustomItem(File file);

    void reloadCustomItems();

    boolean isCustomItem(String itemId);

    boolean isCustomItem(ItemStack itemStack);

    Optional<String> getItemId(ItemStack itemStack);

    Set<String> getItemIds();

    void registerListeners(Plugin plugin, String mechanicId, MechanicListener listener);

    void unloadListeners();

    /**
     * Registers a mechanic that custom items can use.
     *
     * @param factory the factory of the mechanic
     * @deprecated register it in {@link fr.maxlego08.menu.api.registry.Registries#MECHANIC}:
     * {@code registries.writable(Registries.MECHANIC).register(plugin, factory)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void registerMechanicFactory(MechanicFactory<?> factory);

    /**
     * Finds a mechanic by the id written in a custom item file.
     *
     * @param mechanicId the id of the mechanic
     * @return the factory, if one has this id
     */
    Optional<MechanicFactory<?>> getMechanicFactory(String mechanicId);

    void giveItem(Player player, String itemId);

    void executeCheckInventoryItems(Player player);
}
