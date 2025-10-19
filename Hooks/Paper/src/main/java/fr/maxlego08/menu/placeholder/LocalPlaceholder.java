package fr.maxlego08.menu.placeholder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.interfaces.ReturnBiConsumer;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalPlaceholder {

    /**
     * static Singleton instance.
     */
    private static volatile LocalPlaceholder instance;
    private final String prefix = "zmenu";
    private final Pattern pattern = Pattern.compile("[%]"+this.prefix+"_([^%]+)[%]");
    private final List<AutoPlaceholder> autoPlaceholders = new ArrayList<>();
    private MenuPlugin plugin;

    /**
     * Private constructor for singleton.
     */
    private LocalPlaceholder() {
    }

    /**
     * Return a singleton instance of ZPlaceholderApi.
     */
    public static LocalPlaceholder getInstance() {
        // Double lock for thread safety.
        if (instance == null) {
            synchronized (LocalPlaceholder.class) {
                if (instance == null) {
                    instance = new LocalPlaceholder();
                }
            }
        }
        return instance;
    }

    public void register(String startWith, ReturnBiConsumer<OfflinePlayer, String, String> biConsumer) {
        this.autoPlaceholders.add(new AutoPlaceholder(startWith, biConsumer));
    }

    /**
     * @param offlinePlayer
     * @param placeholder
     * @return replaced string
     */
    public String setPlaceholders(OfflinePlayer offlinePlayer, String placeholder) {

        if (placeholder == null || !placeholder.contains("%")) {
            return placeholder;
        }

        Matcher matcher = this.pattern.matcher(placeholder);
        while (matcher.find()) {
            String stringPlaceholder = matcher.group(0);

            String regex = matcher.group(1);
            String replace = this.onRequest(offlinePlayer, regex);

            if (replace != null) {
                placeholder = placeholder.replace(stringPlaceholder, replace);
            }
        }

        return placeholder;
    }

    public List<String> setPlaceholders(OfflinePlayer offlinePlayer, List<String> lore) {
        if (lore == null) {
            return null;
        }
        List<String> parsed = new ArrayList<>(lore.size());
        for (String entry : lore) {
            parsed.add(setPlaceholders(offlinePlayer, entry));
        }
        return parsed;
    }

    public String onRequest(OfflinePlayer offlinePlayer, String string) {

        for (AutoPlaceholder autoPlaceholder : this.autoPlaceholders) {
            if (!string.startsWith(autoPlaceholder.startWith())) {
                continue;
            }
            String value = string.replace(autoPlaceholder.startWith(), "");
            return autoPlaceholder.accept(offlinePlayer, value);
        }

        return null;
    }

    public String getPrefix() {
        return prefix;
    }

    public MenuPlugin getPlugin() {
        return plugin;
    }

    /**
     * Set plugin instance
     *
     * @param plugin
     */
    public void setPlugin(MenuPlugin plugin) {
        this.plugin = plugin;
    }

}
