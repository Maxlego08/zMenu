package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.RarityComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemRarity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotRarityItemComponentLoader extends ItemComponentLoader {

    public SpigotRarityItemComponentLoader(){
        super("rarity");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;
        ItemRarity rarity = ItemRarity.COMMON;
        try {
            rarity = ItemRarity.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        return new RarityComponent(rarity);
    }
}
