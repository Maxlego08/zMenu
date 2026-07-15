package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.PaperMapDecorationsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.registry.RegistryKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperResolvableMapDecorationEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCursor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.20.5")
@PaperOnly
public class PaperMapDecorationsItemComponentLoader extends ItemComponentLoader {

    public PaperMapDecorationsItemComponentLoader(){
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
        return decorations.isEmpty() ? null : new PaperMapDecorationsComponent(decorations);
    }
}
