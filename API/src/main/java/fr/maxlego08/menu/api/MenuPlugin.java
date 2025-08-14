package fr.maxlego08.menu.api;

import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.interfaces.ReturnBiConsumer;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface MenuPlugin extends Plugin {

    PlatformScheduler getScheduler();

    InventoryManager getInventoryManager();

    ButtonManager getButtonManager();

    PatternManager getPatternManager();

    <T> T getProvider(Class<T> headManagerClass);

    String parse(Player player, String string);

    String parse(OfflinePlayer offlinePlayer, String string);

    List<String> parse(Player player, List<String> strings);

    List<String> parse(OfflinePlayer offlinePlayer, List<String> strings);

    InventoriesPlayer getInventoriesPlayer();

    Map<String, Object> getGlobalPlaceholders();

    FontImage getFontImage();

    DataManager getDataManager();

    DupeManager getDupeManager();

    Enchantments getEnchantments();

    MetaUpdater getMetaUpdater();

    CommandManager getCommandManager();

    StorageManager getStorageManager();

    boolean isPaper();

    boolean isFolia();

    void registerPlaceholder(String startWith, ReturnBiConsumer<OfflinePlayer, String, String> biConsumer);

    /**
     * Loads a menu item stack from a YAML configuration and applies global placeholders.
     * This method loads the item stack using the given configuration and path, and
     * applies any global placeholders that have been registered.
     *
     * @param configuration the YAML configuration containing the item stack settings.
     * @param path          the path within the configuration to load from.
     * @param file          the file from which the configuration was loaded, used for logging errors.
     * @return the loaded menu item stack.
     */
    MenuItemStack loadItemStack(YamlConfiguration configuration, String path, File file);
}
