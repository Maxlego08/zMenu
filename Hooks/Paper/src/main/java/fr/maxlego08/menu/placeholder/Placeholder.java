package fr.maxlego08.menu.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface Placeholder {

    String setPlaceholders(Player player, String string);

    String setPlaceholders(OfflinePlayer player, String string);

    List<String> setPlaceholders(Player player, List<String> list);

    List<String> setPlaceholders(OfflinePlayer player, List<String> list);

    class Api implements Placeholder {
        private final Placeholder localPlaceholder;

        public Api() {
            PlaceholderExpansion expansion = new DistantPlaceholder(LocalPlaceholder.getInstance());
            expansion.register();
            localPlaceholder = new Local();
        }

        @Override
        public String setPlaceholders(Player player, String string) {
            return PlaceholderAPI.setPlaceholders(player, string);
        }

        @Override
        public String setPlaceholders(OfflinePlayer player, String string) {
            return PlaceholderAPI.setPlaceholders(player, localPlaceholder.setPlaceholders(player, string));
        }

        @Override
        public List<String> setPlaceholders(Player player, List<String> list) {
            return PlaceholderAPI.setPlaceholders(player, list);
        }

        @Override
        public List<String> setPlaceholders(OfflinePlayer player, List<String> list) {
            return PlaceholderAPI.setPlaceholders(player, list);
        }

    }

    class Local implements Placeholder {

        @Override
        public String setPlaceholders(Player player, String string) {
            return LocalPlaceholder.getInstance().setPlaceholders(player, string);
        }

        @Override
        public String setPlaceholders(OfflinePlayer player, String string) {
            return LocalPlaceholder.getInstance().setPlaceholders(player, string);
        }

        @Override
        public List<String> setPlaceholders(Player player, List<String> list) {
            return LocalPlaceholder.getInstance().setPlaceholders(player, list);
        }

        @Override
        public List<String> setPlaceholders(OfflinePlayer player, List<String> list) {
            return LocalPlaceholder.getInstance().setPlaceholders(player, list);
        }

    }

    class Placeholders {

        private static Placeholder placeholder;

        public static Placeholder getPlaceholder() {
            return placeholder == null ? placeholder = (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null ? new Api() : new Local()) : placeholder;
        }
    }

}
