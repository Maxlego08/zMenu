package fr.maxlego08.menu.api.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Replaces PlaceholderAPI's placeholders in text, and zMenu's own {@code %zmenu_...%} ones.
 *
 * @deprecated zMenu's text engine asks PlaceholderAPI (with {@code cache-placeholder-api}) and zMenu's own placeholders
 * itself, for the viewer, while it renders: compile the text once and render it with
 * {@code TextService.get().flow()} ({@code format(text, placeholders.values(), player)} for a component,
 * {@code formatText(...)} for a {@code String}), or {@code PaperMetaUpdater#render}. The zMenu PlaceholderAPI
 * expansion is still registered at enable.
 */
@Deprecated(since = "1.1.2.2")
public interface Placeholder {

    String setPlaceholders(Player player, String string);

    String setPlaceholders(OfflinePlayer player, String string);

    List<String> setPlaceholders(Player player, List<String> list);

    List<String> setPlaceholders(OfflinePlayer player, List<String> list);

    /**
     * @deprecated see {@link Placeholder}.
     */
    @Deprecated(since = "1.1.2.2")
    class Api implements Placeholder {
        private final Placeholder localPlaceholder;

        public Api() {
            PlaceholderExpansion expansion = new fr.maxlego08.menu.api.placeholder.DistantPlaceholder(fr.maxlego08.menu.api.placeholder.LocalPlaceholder.getInstance());
            expansion.register();
            this.localPlaceholder = new Local();
        }

        @Override
        public String setPlaceholders(Player player, String string) {
            return PlaceholderAPI.setPlaceholders(player, string).replace("\uF000", "%");
        }

        @Override
        public String setPlaceholders(OfflinePlayer player, String string) {
            return PlaceholderAPI.setPlaceholders(player, this.localPlaceholder.setPlaceholders(player, string)).replace("\uF000", "%");
        }

        @Override
        public List<String> setPlaceholders(Player player, List<String> list) {
            return PlaceholderAPI.setPlaceholders(player, list).stream()
                    .map(s -> s.replace("\uF000", "%"))
                    .toList();
        }

        @Override
        public List<String> setPlaceholders(OfflinePlayer player, List<String> list) {
            return PlaceholderAPI.setPlaceholders(player, list).stream()
                    .map(s -> s.replace("\uF000", "%"))
                    .toList();
        }

    }

    /**
     * @deprecated see {@link Placeholder}.
     */
    @Deprecated(since = "1.1.2.2")
    class Local implements Placeholder {

        private static String restorePercent(String string) {
            return string == null ? null : string.replace("", "%");
        }

        private static List<String> restorePercent(List<String> list) {
            return list == null ? null : list.stream().map(Local::restorePercent).toList();
        }

        @Override
        public String setPlaceholders(Player player, String string) {
            return restorePercent(LocalPlaceholder.getInstance().setPlaceholders(player, string));
        }

        @Override
        public String setPlaceholders(OfflinePlayer player, String string) {
            return restorePercent(LocalPlaceholder.getInstance().setPlaceholders(player, string));
        }

        @Override
        public List<String> setPlaceholders(Player player, List<String> list) {
            return restorePercent(LocalPlaceholder.getInstance().setPlaceholders(player, list));
        }

        @Override
        public List<String> setPlaceholders(OfflinePlayer player, List<String> list) {
            return restorePercent(LocalPlaceholder.getInstance().setPlaceholders(player, list));
        }

    }

    /**
     * @deprecated see {@link Placeholder}.
     */
    @Deprecated(since = "1.1.2.2")
    class Placeholders {

        private static Placeholder placeholder;

        public static Placeholder getPlaceholder() {
            return placeholder == null ? placeholder = (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ? new Api() : new Local()) : placeholder;
        }
    }

}
