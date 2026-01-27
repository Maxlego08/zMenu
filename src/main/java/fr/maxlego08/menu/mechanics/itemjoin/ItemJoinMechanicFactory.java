package fr.maxlego08.menu.mechanics.itemjoin;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.io.File;

public class ItemJoinMechanicFactory extends MechanicFactory<ItemJoinMechanic> {
    public ItemJoinMechanicFactory(MenuPlugin plugin) {
        super(plugin, "itemjoin");
        plugin.getItemManager().registerListeners(plugin, "itemjoin", new ItemJoinMechanicListener(this, plugin));
    }

    @Override
    public @NonNull ItemJoinMechanic parse(MenuPlugin plugin, String itemId, ConfigurationSection mechanicSection, YamlConfiguration configurationFile, File file, String path) {
        ItemJoinMechanic mechanic = new ItemJoinMechanic(itemId, this, mechanicSection);
        this.addToImplemented(mechanic);
        return mechanic;
    }
}
