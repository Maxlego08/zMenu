package fr.maxlego08.menu.api.configuration;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public interface ConfigManagerInt {
    <T> void registerConfig(Class<T> configClass, Plugin plugin);
    List<String> getRegisteredConfigs();
    void openConfig(String pluginName, Player player);
    void openConfig(Plugin plugin, Player player);
}
