package fr.maxlego08.menu.loader.components;

import fr.maxlego08.common.loader.components.AbstractEffectItemComponentLoader;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.SuspiciousStewEffectsComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SuspiciousStewEffectsItemComponentLoader extends AbstractEffectItemComponentLoader {

    public SuspiciousStewEffectsItemComponentLoader() {
        super("suspicious_stew_effects");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        List<Map<?, ?>> effects = configuration.getMapList(path);
        List<PotionEffect> potionEffects = parsePotionEffects(effects);
        return potionEffects.isEmpty() ? null : new SuspiciousStewEffectsComponent(potionEffects);
    }
}
