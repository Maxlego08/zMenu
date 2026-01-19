package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.EnchantementsComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SpigotEnchantmentsItemComponentLoader extends ItemComponentLoader {

    public SpigotEnchantmentsItemComponentLoader(){
        super("enchantments");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> values = configuration.getValues(false);
        Map<Enchantment,Integer> enchantments = new HashMap<>();
        for (var entry : values.entrySet()) {
            String enchantmentName = entry.getKey();
            NamespacedKey key = NamespacedKey.fromString(enchantmentName);
            if (key == null) continue;
            try {
                Enchantment enchantment = Registry.ENCHANTMENT.getOrThrow(key);
                int level = componentSection.getInt(enchantmentName, -1);
                if (level > 0) {
                    enchantments.put(enchantment, level);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return enchantments.isEmpty() ? null : new EnchantementsComponent(enchantments);
    }
}
