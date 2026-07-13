package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.PaperPotDecorationsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class PaperPotDecorationsItemComponentLoader extends ItemComponentLoader {
    private static final int SIDES = 4;

    public PaperPotDecorationsItemComponentLoader() {
        super("pot-decorations");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        List<String> decorations = configuration.getStringList(path);

        if (decorations.size() < SIDES) return null;

        ResolvableRegistryEntry<ItemType>[] keys = new ResolvableRegistryEntry[SIDES];
        for (int i = 0; i < SIDES; i++) {
            keys[i] = ResolvableRegistry.auto(decorations.get(i), RegistryKey.ITEM);
        }

        return new PaperPotDecorationsComponent(keys);
    }
}
