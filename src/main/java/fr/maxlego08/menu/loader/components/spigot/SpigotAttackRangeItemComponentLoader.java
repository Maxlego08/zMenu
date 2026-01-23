package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.AttackRangeComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotAttackRangeItemComponentLoader extends ItemComponentLoader {

    public SpigotAttackRangeItemComponentLoader() {
        super("attack-range");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            return null;
        }
        double minReach = componentSection.getDouble("min-reach", 0f);
        if (minReach > 64f || minReach < 0f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid min-reach value in attack-range component at path: " + path + ". Value: " + minReach + ". It must be between 0 and 64. Using default value 0f.");
            minReach = 0f;
        }
        double maxReach = componentSection.getDouble("max-reach", 3f);
        if (maxReach > 64f || maxReach < 0f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid max-reach value in attack-range component at path: " + path + ". Value: " + maxReach + ". It must be between 0 and 64. Using default value 3f.");
            maxReach = 3f;
        }
        double minCreativeReach = componentSection.getDouble("min-creative-reach", 0f);
        if (minCreativeReach > 64f || minCreativeReach < 0f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid min-creative-reach value in attack-range component at path: " + path + ". Value: " + minCreativeReach + ". It must be between 0 and 64. Using default value 0f.");
            minCreativeReach = 0f;
        }
        double maxCreativeReach = componentSection.getDouble("max-creative-reach", 5f);
        if (maxCreativeReach > 64f || maxCreativeReach < 0f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid max-creative-reach value in attack-range component at path: " + path + ". Value: " + maxCreativeReach + ". It must be between 0 and 64. Using default value 5f.");
            maxCreativeReach = 5f;
        }
        double hitboxMargin = componentSection.getDouble("hitbox-margin", 0.3f);
        if (hitboxMargin < 0f || hitboxMargin > 1f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid hitbox-margin value in attack-range component at path: " + path + ". Value: " + hitboxMargin + ". It must be between 0 and 1. Using default value 0.3f.");
            hitboxMargin = 0.3f;
        }
        double mobFactor = componentSection.getDouble("mob-factor", 1f);
        if (mobFactor < 0f || mobFactor > 2f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid mob-factor value in attack-range component at path: " + path + ". Value: " + mobFactor + ". It must be between 0 and 2. Using default value 1f.");
            mobFactor = 1f;
        }
        return new AttackRangeComponent(
                (float) minReach,
                (float) maxReach,
                (float) minCreativeReach,
                (float) maxCreativeReach,
                (float) hitboxMargin,
                (float) mobFactor
        );
    }
}
