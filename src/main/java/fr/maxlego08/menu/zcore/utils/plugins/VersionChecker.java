package fr.maxlego08.menu.zcore.utils.plugins;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author Maxlego08
 */
public class VersionChecker extends ZUtils implements Listener {

    private final String URL_API = "https://groupez.dev/api/v1/resource/version/%s";
    private final String URL_RESOURCE = "https://groupez.dev/resources/%s";
    private final ZMenuPlugin plugin;
    private final int pluginID;
    private boolean useLastVersion = false;

    /**
     * Class constructor
     *
     * @param plugin
     * @param pluginID
     */
    public VersionChecker(ZMenuPlugin plugin, int pluginID) {
        super();
        this.plugin = plugin;
        this.pluginID = pluginID;
    }

    /**
     * Allows to check if the plugin version is up-to-date.
     */
    public void useLastVersion() {
        if (Config.skipUpdateCheck) {
            return;
        }

        String pluginVersion = plugin.getDescription().getVersion();
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        this.getVersion(version -> {
            long ver = Long.parseLong(version.replace(".", ""));
            long plVersion = Long.parseLong(pluginVersion.replace(".", ""));
            atomicBoolean.set(plVersion >= ver);
            this.useLastVersion = atomicBoolean.get();
            if (useLastVersion) {
                Logger.info("No update available.");
            } else {
                Logger.info("New update available. Your version: " + pluginVersion + ", latest version: " + version);
                Logger.info("Download plugin here: " + String.format(URL_RESOURCE, this.pluginID));
            }
        });

    }

    /**
     * Get version by plugin id
     *
     * @param consumer - Do something after
     */
    public void getVersion(Consumer<String> consumer) {
        plugin.getScheduler().runAsync(w -> {
            final String apiURL = String.format(URL_API, this.pluginID);
            try {
                URL url = URI.create(apiURL).toURL();
                URLConnection hc = url.openConnection();
                hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                Scanner scanner = new Scanner(hc.getInputStream());
                if (scanner.hasNext()) consumer.accept(scanner.next());
                scanner.close();

            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}
