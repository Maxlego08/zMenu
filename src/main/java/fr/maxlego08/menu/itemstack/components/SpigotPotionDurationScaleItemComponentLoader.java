package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotPotionDurationScaleItemComponentLoader extends ItemComponentLoader {

    public SpigotPotionDurationScaleItemComponentLoader(){
        super("potion_duration_scale");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        float durationScale = (float) configuration.getDouble(path, 1.0);
        return new PotionDurationScaleComponent(durationScale);
    }
}
