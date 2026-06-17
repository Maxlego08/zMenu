package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.SimpleResolvable;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    protected SimpleResolvable<Float> getMinReachResolvable(ConfigurationSection section, String path) {
        if (section.isString("min-reach")) {
            String expression = section.getString("min-reach", "0f");
            return SimpleResolvable.ofExpression(expression, Float::parseFloat);
        } else {
            float minReach = this.getMinReach(section, path);
            return SimpleResolvable.of(minReach, Float::parseFloat);
        }
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

    protected @NotNull SimpleResolvable<Float> getMaxReachResolvable(ConfigurationSection section, String path) {
        if (section.isString("max-reach")) {
            String expression = section.getString("max-reach", "3f");
            return SimpleResolvable.ofExpression(expression, Float::parseFloat);
        } else {
            float maxReach = this.getMaxReach(section, path);
            return SimpleResolvable.of(maxReach, Float::parseFloat);
        }
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

    protected @NotNull SimpleResolvable<Float> getMinCreativeReachResolvable(ConfigurationSection section, String path) {
        if (section.isString("min-creative-reach")) {
            String expression = section.getString("min-creative-reach", "0f");
            return SimpleResolvable.ofExpression(expression, Float::parseFloat);
        } else {
            float minCreativeReach = this.getMinCreativeReach(section, path);
            return SimpleResolvable.of(minCreativeReach, Float::parseFloat);
        }
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

    protected @NotNull SimpleResolvable<Float> getMaxCreativeReachResolvable(ConfigurationSection section, String path) {
        if (section.isString("max-creative-reach")) {
            String expression = section.getString("max-creative-reach", "5f");
            return SimpleResolvable.ofExpression(expression, Float::parseFloat);
        } else {
            float maxCreativeReach = this.getMaxCreativeReach(section, path);
            return SimpleResolvable.of(maxCreativeReach, Float::parseFloat);
        }
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

    protected @NotNull SimpleResolvable<Float> getHitboxMarginResolvable(ConfigurationSection section, String path) {
        if (section.isString("hitbox-margin")) {
            String expression = section.getString("hitbox-margin", "0.3f");
            return SimpleResolvable.ofExpression(expression, Float::parseFloat);
        } else {
            float hitboxMargin = this.getHitboxMargin(section, path);
            return SimpleResolvable.of(hitboxMargin, Float::parseFloat);
        }
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

    protected @NotNull SimpleResolvable<Float> getMobFactorResolvable(ConfigurationSection section, String path) {
        if (section.isString("mob-factor")) {
            String expression = section.getString("mob-factor", "1f");
            return SimpleResolvable.ofExpression(expression, Float::parseFloat);
        } else {
            float mobFactor = this.getMobFactor(section, path);
            return SimpleResolvable.of(mobFactor, Float::parseFloat);
        }
    }
}
