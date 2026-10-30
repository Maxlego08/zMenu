package fr.maxlego08.menu.api.itemstack;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * Reads, converts and compares the items of zMenu.
 *
 * @see fr.maxlego08.menu.api.MenuPlugin#getItemStackService()
 */
public interface ItemStackService {

    /**
     * Loads a menu item from a YAML configuration, with the global placeholders applied.
     *
     * @param configuration the configuration holding the item
     * @param path          the path of the item in the configuration, ending with a dot
     * @param file          the file the configuration was read from, named when an error is logged
     * @return the menu item
     */
    MenuItemStack load(YamlConfiguration configuration, String path, File file);

    /**
     * Loads a menu item from a map of its settings, such as the item of an action.
     *
     * @param file the file the map was read from, named when an error is logged
     * @param map  the settings of the item
     * @return the menu item
     */
    MenuItemStack load(File file, Map<String, Object> map);

    /**
     * Turns an item into a menu item, with item components for every setting the item changes.
     * When a setting cannot be expressed, the whole item is kept as {@code base64:} so that nothing is lost.
     *
     * @param itemStack the item to convert
     * @return the menu item, and the components that forced the fallback
     */
    @NotNull
    MenuItemStackConversion fromItemStack(@NotNull ItemStack itemStack);

    /**
     * Writes an item as a base64 string.
     *
     * @param itemStack the item
     * @return the item as base64
     */
    String serialize(ItemStack itemStack);

    /**
     * Reads an item written by {@link #serialize(ItemStack)}.
     *
     * @param serializedItemStack the item as base64
     * @return the item
     */
    ItemStack deserialize(String serializedItemStack);

    /**
     * Gives a player head the skin of the player named by the {@code player-head} of a button.
     *
     * @param itemStack    the head
     * @param button       the button, whose player head is read
     * @param player       the player the placeholders are parsed for
     * @param placeholders the placeholders of the inventory
     * @return the head
     */
    ItemStack applyPlayerHead(ItemStack itemStack, Button button, Player player, Placeholders placeholders);

    /**
     * Gets a way to compare items by its name as written in a file, whatever its case. Comparisons
     * are registered in {@link fr.maxlego08.menu.api.registry.Registries#ITEM_VERIFICATION}.
     *
     * @param name the name of the comparison
     * @return the comparison, if one has this name
     */
    Optional<ItemStackSimilar> getVerification(String name);
}
