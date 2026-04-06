package fr.maxlego08.menu.hooks.mmoitems;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemsLoader extends MaterialLoader {

    public MMOItemsLoader() {
        super("mmoitems");
    }

    @Override
    public @Nullable ItemStack load(@NotNull Player player, @Nullable YamlConfiguration configuration, @NotNull String path, @NotNull String materialString) {
        String[] split = materialString.split(":",2);
        if (split.length != 2) return null;
        try {
            final Type type = MMOItems.plugin.getTypes().get(split[0]);
            if (type == null) return null;
            return MMOItems.plugin.getItem(type, split[1]);
        } catch (Exception e) {
            return null;
        }
    }
}
