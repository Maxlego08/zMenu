package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.UseEffectsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
public class SpigotUseEffectsItemComponentLoader extends ItemComponentLoader {

    public SpigotUseEffectsItemComponentLoader(){
        super("use-effects");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        ResolvableBoolean canSprint = this.asResolvableBoolean(componentSection, "can-sprint", false);
        ResolvableFloat speedMultiplier = this.asResolvableFloat(componentSection, "speed-multiplier", 0.2f);
        ResolvableBoolean interactVibrations = this.asResolvableBoolean(componentSection, "interact-vibrations", true);
        return new UseEffectsComponent(canSprint, speedMultiplier, interactVibrations);
    }
}
