package fr.maxlego08.menu;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Entry point declared by {@code plugin.yml}, only ever used when the server cannot read
 * {@code paper-plugin.yml}.
 *
 * <p>zMenu is a Paper plugin: it ships a bootstrapper and a plugin loader, and relies on APIs
 * Spigot does not provide, so the real plugin is declared through {@code paper-plugin.yml} only.
 * Paper always prefers {@code paper-plugin.yml} when both descriptors are present
 * ({@code PluginFileType#guessType} walks {@code List.of(PAPER, SPIGOT)} and returns the first one
 * found in the jar), so {@link ZMenuPlugin} stays the entry point on Paper and on every Paper fork.
 *
 * <p>With no {@code plugin.yml} at all, Spigot rejects the jar outright with "does not contain
 * plugin.yml", which reads like a corrupted download. This class exists so the plugin loads, stays
 * enabled, and tells whoever administrates the server - in the console and when they join - that
 * zMenu needs Paper. No zMenu feature is available in this mode.
 */
public class ZMenuSpigotFallback extends JavaPlugin implements Listener {

    /**
     * Permission required to receive the warning on join. Declared with {@code default: op} in
     * {@code plugin.yml}: the message is meant for whoever administrates the server, not for every
     * player connecting to it.
     */
    private static final String WARNING_PERMISSION = "zmenu.paper.warning";

    private static final String DOWNLOAD_URL = "https://papermc.io/downloads";

    /**
     * Delay, in ticks, before the join warning is sent, so it is not buried under the MOTD and the
     * welcome messages other plugins send at the same moment.
     */
    private static final long JOIN_MESSAGE_DELAY = 40L;

    @Override
    public void onEnable() {
        this.logConsoleWarning();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(WARNING_PERMISSION)) return;

        this.getServer().getScheduler().runTaskLater(this, () -> {
            if (!player.isOnline()) return;
            for (String line : this.joinMessage()) {
                player.sendMessage(color(line));
            }
        }, JOIN_MESSAGE_DELAY);
    }

    private void logConsoleWarning() {
        Logger logger = this.getLogger();
        logger.warning("");
        logger.warning("*************************************************************");
        logger.warning("  zMenu could not start: no menu, command or API is available.");
        logger.warning("");

        if (isPaperServer()) {
            logger.warning("  This server runs Paper, but this build is too old to read");
            logger.warning("  paper-plugin.yml. Update your server to a recent Paper build.");
        } else {
            logger.warning("  zMenu is a Paper plugin and this server runs Spigot.");
            logger.warning("  Install Paper, or a fork such as Purpur or Folia, to use it.");
            logger.warning("");
            logger.warning("  Paper is a drop-in replacement for Spigot: keep your worlds,");
            logger.warning("  your plugins and your configuration, swap the server jar.");
        }

        logger.warning("");
        logger.warning("  Download: " + DOWNLOAD_URL);
        logger.warning("*************************************************************");
        logger.warning("");
    }

    private String[] joinMessage() {
        String platform = isPaperServer()
                ? "&7This &aPaper &7build is too old to load zMenu."
                : "&7This server runs &cSpigot&7, zMenu requires &aPaper&7.";

        return new String[]{
                "&8&m                                                        ",
                " &c&lzMenu is not running",
                " " + platform,
                " &7Download Paper: &f" + DOWNLOAD_URL,
                "&8&m                                                        ",
        };
    }

    /**
     * Whether the server is Paper or a Paper fork. Reaching this class on such a server means the
     * build predates {@code paper-plugin.yml} support, which needs a different message than the
     * Spigot one.
     */
    private static boolean isPaperServer() {
        return hasClass("io.papermc.paper.configuration.Configuration")
                || hasClass("com.destroystokyo.paper.PaperConfig");
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    private static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
