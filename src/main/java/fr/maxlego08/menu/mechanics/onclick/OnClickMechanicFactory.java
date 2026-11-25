package fr.maxlego08.menu.mechanics.onclick;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class OnClickMechanicFactory extends MechanicFactory<OnClickMechanic> {

    public OnClickMechanicFactory(MenuPlugin plugin) {
        super(plugin, "on-click");
        plugin.getItemManager().registerListeners(plugin, "on-click", new OnClickMechanicListener(this, plugin));
    }

    @Override
    public @NonNull OnClickMechanic parse(MenuPlugin plugin, String itemId,
                                           Section mechanicSection,
                                           YamlDocument configuration, File file, String path) {
        OnClickMechanic mechanic = new OnClickMechanic(itemId, this, mechanicSection, configuration, file, path);
        this.addToImplemented(mechanic);
        return mechanic;
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public OnClickMechanic parse(MenuPlugin plugin, String itemId, ConfigurationSection mechanicSection, YamlConfiguration configuration, File file, String path) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.parse(plugin, itemId, YamlSections.toSection(configurationDocument, mechanicSection), configurationDocument, file, path);
    }
}
