package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.ResolvablePersistentDataEntry;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.CustomDataComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotCustomDataItemComponentLoader extends ItemComponentLoader {

    public SpigotCustomDataItemComponentLoader() {
        super("custom-data");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        List<ResolvablePersistentDataEntry> pdcEntries = new ArrayList<>();

        for (String key : componentSection.getKeys(false)) {
            Object value = componentSection.get(key);
            if (value == null) continue;

            ResolvablePersistentDataEntry entry = ResolvablePersistentDataEntry.fromKeyValue(key, value);
            if (entry != null) {
                pdcEntries.add(entry);
            }
        }

        return pdcEntries.isEmpty() ? null : new CustomDataComponent(pdcEntries);
    }
}
