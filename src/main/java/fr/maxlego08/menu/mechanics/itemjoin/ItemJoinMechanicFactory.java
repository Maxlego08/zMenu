package fr.maxlego08.menu.mechanics.itemjoin;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class ItemJoinMechanicFactory extends MechanicFactory<ItemJoinMechanic> {
    public ItemJoinMechanicFactory(MenuPlugin plugin) {
        super(plugin, "itemjoin");
        plugin.getItemManager().registerListeners(plugin, "itemjoin", new ItemJoinMechanicListener(this, plugin));
    }

    @Override
    public @NonNull ItemJoinMechanic parse(MenuPlugin plugin, String itemId, Section mechanicSection, YamlDocument configurationFile, File file, String path) {
        ItemJoinMechanic mechanic = new ItemJoinMechanic(itemId, this, mechanicSection);
        this.addToImplemented(mechanic);
        return mechanic;
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemJoinMechanic parse(MenuPlugin plugin, String itemId, ConfigurationSection mechanicSection, YamlConfiguration configurationFile, File file, String path) {
        YamlDocument configurationFileDocument = configurationFile == null ? null : YamlSections.toDocument(configurationFile);
        return this.parse(plugin, itemId, YamlSections.toSection(configurationFileDocument, mechanicSection), configurationFileDocument, file, path);
    }
}
