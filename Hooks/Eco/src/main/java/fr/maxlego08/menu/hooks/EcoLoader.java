package fr.maxlego08.menu.hooks;

import com.willfp.eco.core.items.Items;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class EcoLoader extends MaterialLoader {
    
    public EcoLoader() {
        super("eco");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration yamlConfiguration, @NonNull String path, @NonNull String materialString) {
        try {
            //eco item lookup system:
            // https://plugins.auxilor.io/all-plugins/the-item-lookup-system
            return Items.lookup(materialString).getItem();
        } catch (Exception ignored) {
            return null;
        }
    }
}