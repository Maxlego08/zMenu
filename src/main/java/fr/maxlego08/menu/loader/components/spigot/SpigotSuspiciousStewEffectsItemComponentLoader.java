package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.SuspiciousStewEffectsComponent;
import fr.maxlego08.menu.loader.components.AbstractEffectItemComponentLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SpigotSuspiciousStewEffectsItemComponentLoader extends AbstractEffectItemComponentLoader {

    public SpigotSuspiciousStewEffectsItemComponentLoader() {
        super("suspicious_stew_effects");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        List<Map<?, ?>> effects = configuration.getMapList(path);
        List<PotionEffect> potionEffects = parsePotionEffects(effects);
        return potionEffects.isEmpty() ? null : new SuspiciousStewEffectsComponent(potionEffects);
    }
}
