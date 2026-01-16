package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.StoredEnchantmentsComponent;
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

public class StoredEnchantItemComponentLoader extends ItemComponentLoader {

    public StoredEnchantItemComponentLoader(){
        super("stored_enchantments");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> values = componentSection.getValues(false);
        Map<Enchantment,Integer> storedEnchants = new HashMap<>();
        for (Map.Entry<String, Object> entry : values.entrySet()){
            String enchantName = entry.getKey();
            NamespacedKey enchantKey = NamespacedKey.fromString(enchantName);
            if (enchantKey == null) continue;
            try {
                Enchantment enchantment = Registry.ENCHANTMENT.get(enchantKey);
                int level = componentSection.getInt(enchantName);
                if (enchantment != null && level > 0){
                    storedEnchants.put(enchantment, level);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return storedEnchants.isEmpty() ? null : new StoredEnchantmentsComponent(storedEnchants);
    }
}
