package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.FireworksComponent;
import fr.maxlego08.menu.loader.components.AbstractFireworkItemComponentLoader;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpigotFireworksItemComponentLoader extends AbstractFireworkItemComponentLoader {

    public SpigotFireworksItemComponentLoader(){
        super("fireworks");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        int flightDuration = componentSection.getInt("flight_duration", 1);
        List<FireworkEffect> effects = new ArrayList<>();
        List<Map<?, ?>> mapList = componentSection.getMapList("explosions");
        for (var rawMap : mapList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> effectMap = (Map<String, Object>) rawMap;
            loadFireworkEffect(effectMap).ifPresent(effects::add);
        }
        return effects.isEmpty() && flightDuration == 1 ? null : new FireworksComponent(flightDuration, effects);
    }
}
