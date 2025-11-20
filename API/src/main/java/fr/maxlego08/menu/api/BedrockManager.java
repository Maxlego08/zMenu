package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.configuration.ConfigManagerInt;
import fr.maxlego08.menu.api.exceptions.DialogException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BedrockManager {

    BedrockInventory loadBedrockInventory(Plugin plugin, String fileName) throws DialogException;

    Optional<BedrockInventory> getBedrockInventory(String name);

    Optional<BedrockInventory> getBedrockInventory(String pluginName, String fileName);

    Optional<BedrockInventory> getBedrockInventory(Plugin plugin, String fileName);

    void deleteBedrockInventory(String name);

    void deleteBedrockInventory(Plugin plugin);

    void loadBedrockInventory();

    BedrockInventory loadInventory(Plugin plugin, String fileName) throws DialogException, InventoryException;

    BedrockInventory loadInventory(Plugin plugin, File file) throws DialogException, InventoryException;

    BedrockInventory loadInventory(Plugin plugin, String fileName, Class<? extends BedrockInventory> dialogClass) throws DialogException, InventoryException;

    BedrockInventory loadInventory(Plugin plugin, File file, Class<? extends BedrockInventory> dialogClass) throws DialogException, InventoryException;

    void openBedrockInventory(Player player, BedrockInventory bedrockInventory);

    void openBedrockInventory(Player player, BedrockInventory bedrockInventory, List<Inventory> oldInventories);

    Collection<BedrockInventory> getBedrockInventory();

    void reloadBedrockInventory();

    boolean isBedrockPlayer(Player player);

    boolean isBedrockPlayer(String value);
}
