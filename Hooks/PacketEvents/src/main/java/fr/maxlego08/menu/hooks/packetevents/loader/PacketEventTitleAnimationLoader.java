package fr.maxlego08.menu.hooks.packetevents.loader;

import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimationLoader;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.hooks.packetevents.animation.PacketTitleAnimation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class PacketEventTitleAnimationLoader extends TitleAnimationLoader {

    @Override
    public TitleAnimation load(@NotNull YamlConfiguration configuration, @NotNull String path, Object... objects) throws InventoryException {
        return new PacketTitleAnimation(super.loadSettings(configuration, path));
    }
}
