package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.Tuples;
import fr.maxlego08.menu.itemstack.components.AttributeModifiersComponent;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpigotAttributeModifiersItemComponentLoader extends ItemComponentLoader {

    public SpigotAttributeModifiersItemComponentLoader(){
        super("attribute_modifiers");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        List<Map<?, ?>> mapList = configuration.getMapList(path);
        List<Tuples<Attribute, AttributeModifier>> modifiers = new ArrayList<>();
        for (Map<?, ?> rawMap : mapList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            String type = (String) map.get("type");
            if (type == null) continue;
            Attribute attribute;
            try {
                NamespacedKey key = NamespacedKey.fromString(type.toLowerCase());
                if (key == null) continue;
                attribute = Registry.ATTRIBUTE.get(key);
            } catch (IllegalArgumentException e) {
                continue;
            }
            if (attribute == null) continue;
            try {
                AttributeModifier deserialize = AttributeModifier.deserialize(map);
                modifiers.add(new Tuples<>(attribute, deserialize));
            } catch (IllegalArgumentException e) {
                if (Configuration.enableDebug){
                    Logger.info("Error deserializing attribute modifier for attribute " + attribute.name() + ": " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }
        return modifiers.isEmpty() ? null : new AttributeModifiersComponent(modifiers);
    }
}
