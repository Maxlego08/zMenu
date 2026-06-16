package fr.maxlego08.menu.api.configuration;

import fr.maxlego08.menu.api.configuration.dialog.ConfigDialogBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ConfigManagerInt {
    <T> void registerConfig(@NotNull ConfigDialogBuilder configDialogBuilder, @NotNull Class<T> configClass, @NotNull Plugin plugin);
    @NotNull List<String> getRegisteredConfigs();
    void openConfig(@NotNull String pluginName,@NotNull Player player);
    void openConfig(@NotNull Plugin plugin,@NotNull Player player);
}
