package fr.maxlego08.menu.api.interfaces;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface ItemComponentLoaderInterface {
    @Nullable
    ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection);
}
