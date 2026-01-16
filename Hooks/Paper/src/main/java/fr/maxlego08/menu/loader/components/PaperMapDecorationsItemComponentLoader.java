package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.paper.components.PaperMapDecorationsComponent;
import fr.maxlego08.menu.zcore.utils.ZDecorationEntry;
import io.papermc.paper.datacomponent.item.MapDecorations;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PaperMapDecorationsItemComponentLoader extends ItemComponentLoader {

    public PaperMapDecorationsItemComponentLoader(){
        super("map_decorations");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, MapDecorations.DecorationEntry> decorations = new HashMap<>();
        for (String key : componentSection.getKeys(false)) {
            ConfigurationSection decorationSection = componentSection.getConfigurationSection(key);
            if (decorationSection == null) continue;
            String typeName = decorationSection.getString("type");
            if (typeName == null) continue;
            NamespacedKey typeKey = NamespacedKey.fromString(typeName);
            if (typeKey == null) continue;
            try {
                MapCursor.Type type = Registry.MAP_DECORATION_TYPE.getOrThrow(typeKey);
                double x = decorationSection.getDouble("x");
                double z = decorationSection.getDouble("z");
                float rotation = (float) decorationSection.getDouble("rotation", 0);
                MapDecorations.DecorationEntry entry = new ZDecorationEntry(type, x, z, rotation);
                decorations.put(key, entry);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return decorations.isEmpty() ? null : new PaperMapDecorationsComponent(decorations);
    }
}
