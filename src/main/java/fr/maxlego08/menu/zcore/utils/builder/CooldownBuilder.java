package fr.maxlego08.menu.zcore.utils.builder;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownBuilder {

    public static Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    public static Map<UUID, Long> getCooldownMap(String key) {
        return cooldowns.getOrDefault(key, null);
    }

    public static void clear() {
        cooldowns.clear();
    }

    public static void createCooldown(String key) {
        cooldowns.putIfAbsent(key, new HashMap<>());
    }

    public static void removeCooldown(String key, UUID uuid) {

        createCooldown(key);

        getCooldownMap(key).remove(uuid);
    }

    public static void removeCooldown(String key, Player player) {
        removeCooldown(key, player.getUniqueId());
    }

    public static void addCooldown(String key, UUID uuid, int seconds) {

        createCooldown(key);

        long next = System.currentTimeMillis() + seconds * 1000L;
        getCooldownMap(key).put(uuid, next);
    }

    public static void addCooldown(String key, Player player, int seconds) {
        addCooldown(key, player.getUniqueId(), seconds);
    }

    public static boolean isCooldown(String key, UUID uuid) {

        createCooldown(key);
        Map<UUID, Long> map = cooldowns.get(key);

        return (map.containsKey(uuid)) && (System.currentTimeMillis() <= map.get(uuid));
    }

    public static boolean isCooldown(String key, Player player) {
        return isCooldown(key, player.getUniqueId());
    }

    public static long getCooldown(String key, UUID uuid) {

        createCooldown(key);
        Map<UUID, Long> map = cooldowns.get(key);

        return map.getOrDefault(uuid, 0L) - System.currentTimeMillis();
    }

    public static long getCooldownPlayer(String key, Player player) {
        return getCooldown(key, player.getUniqueId());
    }

    public static String getCooldownAsString(String key, UUID player) {
        return TimerBuilder.getStringTime(getCooldown(key, player) / 1000);
    }
}
