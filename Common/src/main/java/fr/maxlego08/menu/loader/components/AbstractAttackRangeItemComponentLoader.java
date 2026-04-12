package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;

public abstract class AbstractAttackRangeItemComponentLoader extends ItemComponentLoader {

    public AbstractAttackRangeItemComponentLoader() {
        super("attack-range");
    }

    protected float getMinReach(ConfigurationSection section, String path) {
        double minReach = section.getDouble("min-reach", 0f);
        if (minReach > 64f || minReach < 0f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid min-reach value in attack-range component at path: " + path + ". Value: " + minReach + ". It must be between 0 and 64. Using default value 0f.");
            minReach = 0f;
        }
        return (float) minReach;
    }

    protected float getMaxReach(ConfigurationSection section, String path) {
        double maxReach = section.getDouble("max-reach", 3f);
        if (maxReach > 64f || maxReach < 0f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid max-reach value in attack-range component at path: " + path + ". Value: " + maxReach + ". It must be between 0 and 64. Using default value 3f.");
            maxReach = 3f;
        }
        return (float) maxReach;
    }

    protected float getMinCreativeReach(ConfigurationSection section, String path) {
        double minCreativeReach = section.getDouble("min-creative-reach", 0f);
        if (minCreativeReach > 64f || minCreativeReach < 0f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid min-creative-reach value in attack-range component at path: " + path + ". Value: " + minCreativeReach + ". It must be between 0 and 64. Using default value 0f.");
            minCreativeReach = 0f;
        }
        return (float) minCreativeReach;
    }

    protected float getMaxCreativeReach(ConfigurationSection section, String path) {
        double maxCreativeReach = section.getDouble("max-creative-reach", 5f);
        if (maxCreativeReach > 64f || maxCreativeReach < 0f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid max-creative-reach value in attack-range component at path: " + path + ". Value: " + maxCreativeReach + ". It must be between 0 and 64. Using default value 5f.");
            maxCreativeReach = 5f;
        }
        return (float) maxCreativeReach;
    }

    protected float getHitboxMargin(ConfigurationSection section, String path) {
        double hitboxMargin = section.getDouble("hitbox-margin", 0.3f);
        if (hitboxMargin < 0f || hitboxMargin > 1f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid hitbox-margin value in attack-range component at path: " + path + ". Value: " + hitboxMargin + ". It must be between 0 and 1. Using default value 0.3f.");
            hitboxMargin = 0.3f;
        }
        return (float) hitboxMargin;
    }

    protected float getMobFactor(ConfigurationSection section, String path) {
        double mobFactor = section.getDouble("mob-factor", 1f);
        if (mobFactor < 0f || mobFactor > 2f) {
            if (Configuration.enableDebug)
                Logger.info("Invalid mob-factor value in attack-range component at path: " + path + ". Value: " + mobFactor + ". It must be between 0 and 2. Using default value 1f.");
            mobFactor = 1f;
        }
        return (float) mobFactor;
    }
}
