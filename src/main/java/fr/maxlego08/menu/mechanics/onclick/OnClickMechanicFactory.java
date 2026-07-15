package fr.maxlego08.menu.mechanics.onclick;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.io.File;

public class OnClickMechanicFactory extends MechanicFactory<OnClickMechanic> {

    public OnClickMechanicFactory(MenuPlugin plugin) {
        super(plugin, "on-click");
        plugin.getItemManager().registerListeners(plugin, "on-click", new OnClickMechanicListener(this, plugin));
    }

    @Override
    public @NonNull OnClickMechanic parse(MenuPlugin plugin, String itemId,
                                           ConfigurationSection mechanicSection,
                                           YamlConfiguration configuration, File file, String path) {
        OnClickMechanic mechanic = new OnClickMechanic(itemId, this, mechanicSection, configuration, file, path);
        this.addToImplemented(mechanic);
        return mechanic;
    }
}
