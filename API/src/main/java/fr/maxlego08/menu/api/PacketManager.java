package fr.maxlego08.menu.api;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PacketManager {

    void onLoad();

    void onEnable();

    void onDisable();

    void editInventoryTitleName(@NotNull Player player, @NotNull Component title);

    void editInventoryTitleName(@NotNull Player player, @NotNull String title);
}
