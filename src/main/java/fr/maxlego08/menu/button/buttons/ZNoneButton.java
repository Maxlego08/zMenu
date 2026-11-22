package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.button.Button;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class ZNoneButton extends Button {

    @Override
    protected void serializeProperties(@NotNull Section section) {
    }

    @Override
    @Deprecated
    protected void serializeProperties(ConfigurationSection section) {
        YamlSections.write(section, copy -> this.serializeProperties(copy));
    }
}
