package fr.maxlego08.menu.hooks.packetevents.animation;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.PlayerTitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimationSettings;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class PacketTitleAnimation implements TitleAnimation {
    private final TitleAnimationSettings settings;

    public PacketTitleAnimation(TitleAnimationSettings settings) {
        this.settings = settings;
    }

    @Override
    public PlayerTitleAnimation playTitleAnimation(@NotNull MenuPlugin plugin, int containerId, @NotNull InventoryType type, int size, Object... args) {
        return new PacketPlayerTitleAnimation(plugin, this.settings, containerId, type, size, args);
    }
}
