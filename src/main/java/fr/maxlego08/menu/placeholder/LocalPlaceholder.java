package fr.maxlego08.menu.placeholder;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.zcore.utils.interfaces.ReturnBiConsumer;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LocalPlaceholder {

    /**
     * static Singleton instance.
     */
    private static volatile LocalPlaceholder instance;
    private final String prefix = "zmenu";
    private final Pattern pattern = Pattern.compile("[%]"+this.prefix+"_([^%]+)[%]");
    private final List<AutoPlaceholder> autoPlaceholders = new ArrayList<>();
    private ZMenuPlugin plugin;

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
        return lore == null ? null : lore.stream().map(e -> e = setPlaceholders(offlinePlayer, e)).collect(Collectors.toList());
    }

    public String onRequest(OfflinePlayer offlinePlayer, String string) {

        Optional<AutoPlaceholder> optional = this.autoPlaceholders.stream().filter(e -> string.startsWith(e.getStartWith())).findFirst();
        if (optional.isPresent()) {

            AutoPlaceholder autoPlaceholder = optional.get();
            String value = string.replace(autoPlaceholder.getStartWith(), "");
            return autoPlaceholder.accept(offlinePlayer, value);

        }

        return null;
    }

    public String getPrefix() {
        return prefix;
    }

    public ZMenuPlugin getPlugin() {
        return plugin;
    }

    /**
     * Set plugin instance
     *
     * @param plugin
     */
    public void setPlugin(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

}
