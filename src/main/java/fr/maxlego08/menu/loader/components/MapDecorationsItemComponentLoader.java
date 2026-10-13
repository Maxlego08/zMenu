package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.MapDecorationsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperResolvableMapDecorationEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapDecorations;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.21.3")
public class MapDecorationsItemComponentLoader extends ItemComponentLoader {

    public MapDecorationsItemComponentLoader(){
        super("map-decorations");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, PaperResolvableMapDecorationEntry> decorations = new HashMap<>();
        for (String key : componentSection.getKeys(false)) {
            ConfigurationSection decorationSection = componentSection.getConfigurationSection(key);
            if (decorationSection == null) continue;
            String typeName = decorationSection.getString("type");
            ResolvableRegistryEntry<MapCursor.Type> mapCursorTypeRegistryEntry = ResolvableRegistry.autoOrNull(typeName, RegistryKey.MAP_DECORATION_TYPE);
            ResolvableInt x = ResolvableInt.autoOrNull(decorationSection.getString("x"));
            ResolvableInt z = ResolvableInt.autoOrNull(decorationSection.getString("z"));
            ResolvableFloat rotation = ResolvableFloat.autoOrNull(decorationSection.getString("rotation"));

            if (mapCursorTypeRegistryEntry == null || x == null || z == null || rotation == null) continue;
            PaperResolvableMapDecorationEntry entry = new PaperResolvableMapDecorationEntry(mapCursorTypeRegistryEntry, x, z, rotation);
            decorations.put(key, entry);
        }
        return decorations.isEmpty() ? null : new MapDecorationsComponent(decorations);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        MapDecorations mapDecorations = itemStack.getData(DataComponentTypes.MAP_DECORATIONS);
        if (mapDecorations == null || mapDecorations.decorations().isEmpty()) return null;

        Map<String, PaperResolvableMapDecorationEntry> decorations = new LinkedHashMap<>();
        for (Map.Entry<String, MapDecorations.DecorationEntry> entry : mapDecorations.decorations().entrySet()) {
            if (entry.getKey().contains(".")) return null;
            MapDecorations.DecorationEntry decoration = entry.getValue();
            if (decoration.x() != (int) decoration.x() || decoration.z() != (int) decoration.z()) return null;
            ResolvableRegistryEntry<MapCursor.Type> type = ResolvableRegistry.ofRegisteredOrNull(decoration.type(), RegistryKey.MAP_DECORATION_TYPE);
            if (type == null) return null;
            decorations.put(entry.getKey(), new PaperResolvableMapDecorationEntry(
                    type,
                    ResolvableInt.of((int) decoration.x()),
                    ResolvableInt.of((int) decoration.z()),
                    ResolvableFloat.of(decoration.rotation())
            ));
        }
        return new MapDecorationsComponent(decorations);
    }
}
