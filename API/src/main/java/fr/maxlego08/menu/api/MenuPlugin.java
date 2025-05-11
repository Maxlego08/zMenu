package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public interface MenuPlugin extends Plugin {

    ZScheduler getScheduler();

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
}
