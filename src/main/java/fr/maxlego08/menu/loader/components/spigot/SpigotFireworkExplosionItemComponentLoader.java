package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.FireworkExplosionComponent;
import fr.maxlego08.menu.loader.components.AbstractFireworkItemComponentLoader;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;

public class SpigotFireworkExplosionItemComponentLoader extends AbstractFireworkItemComponentLoader {

    public SpigotFireworkExplosionItemComponentLoader(){
        super("firework_explosion");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Optional<FireworkEffect> fireworkEffect = loadFireworkEffect(componentSection.getValues(true));
        return fireworkEffect.map(FireworkExplosionComponent::new).orElse(null);
    }
}
