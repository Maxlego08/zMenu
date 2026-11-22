package fr.maxlego08.menu.hooks.packetevents.animation;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.PlayerTitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimationSettings;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class PacketTitleAnimation implements TitleAnimation {
    private final TitleAnimationSettings settings;

    public PacketTitleAnimation(TitleAnimationSettings settings) {
        this.settings = settings;
    }

    public TitleAnimationSettings getSettings() {
        return this.settings;
    }

    @Override
    public PlayerTitleAnimation playTitleAnimation(@NotNull MenuPlugin plugin, int containerId, @NotNull InventoryType type, int size, Object... args) {
        return new PacketPlayerTitleAnimation(plugin, this.settings, containerId, type, size, args);
    }

    @Override
    public void serialize(@NotNull Section section) {
        this.settings.serialize(section);
    }

    @Override
    @Deprecated
    public void serialize(ConfigurationSection section) {
        YamlSections.write(section, copy -> this.serialize(copy));
    }
}
