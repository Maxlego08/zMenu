package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.UseEffectsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotUseEffectsItemComponentLoader extends ItemComponentLoader {

    public SpigotUseEffectsItemComponentLoader(){
        super("use-effects");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        boolean canSprint = componentSection.getBoolean("can-sprint", false);
        float speedMultiplier = (float) componentSection.getDouble("speed-multiplier", 0.2f);
        boolean interactVibrations = componentSection.getBoolean("interact-vibrations", true);
        return new UseEffectsComponent(canSprint, speedMultiplier, interactVibrations);
    }
}
