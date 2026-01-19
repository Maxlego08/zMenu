package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.paper.PaperPotDecorationsComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class PaperPotDecorationsItemComponentLoader extends ItemComponentLoader {
    private static final int SIDES = 4;

    public PaperPotDecorationsItemComponentLoader() {
        super("pot_decorations");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        List<String> decorations = configuration.getStringList(path);

        if (decorations.size() < SIDES) return null;

        ItemType[] types = new ItemType[SIDES];
        for (int i = 0; i < SIDES; i++) {
            types[i] = getItemTypeFromString(decorations.get(i));
            if (types[i] == null) return null;
        }

        return new PaperPotDecorationsComponent(types[0], types[1], types[2], types[3]);
    }

    private @Nullable ItemType getItemTypeFromString(String keyString) {
        try {
            NamespacedKey key = NamespacedKey.fromString(keyString);
            if (key == null) return null;
            return Registry.ITEM.getOrThrow(key);
        } catch (Exception e) {
            return null;
        }
    }
}

