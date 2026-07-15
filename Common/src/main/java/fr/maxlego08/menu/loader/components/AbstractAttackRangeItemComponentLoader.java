package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractAttackRangeItemComponentLoader extends ItemComponentLoader {

    public AbstractAttackRangeItemComponentLoader() {
        super("attack-range");
    }

    @NotNull
    protected ResolvableFloat getMinReachResolvable(ConfigurationSection section, String path) {
        ResolvableFloat resolvable = this.asResolvableFloat(section, "min-reach");
        if (resolvable == null) return ResolvableFloat.of(0f);

        if (!resolvable.isDynamic()) {
            float value = resolvable.getResolvedValue();
            if (value > 64f || value < 0f) {
                if (Configuration.enableDebug)
                    Logger.info("Invalid min-reach value in attack-range component at path: " + path + ". Value: " + value + ". It must be between 0 and 64. Using default value 0f.");
                return ResolvableFloat.of(0f);
            }
        }
        return resolvable;
    }

    protected @NotNull ResolvableFloat getMaxReachResolvable(ConfigurationSection section, String path) {
        ResolvableFloat resolvable = this.asResolvableFloat(section, "max-reach");
        if (resolvable == null) return ResolvableFloat.of(3f);

        if (!resolvable.isDynamic()) {
            float value = resolvable.getResolvedValue();
            if (value > 64f || value < 0f) {
                if (Configuration.enableDebug)
                    Logger.info("Invalid max-reach value in attack-range component at path: " + path + ". Value: " + value + ". It must be between 0 and 64. Using default value 3f.");
                return ResolvableFloat.of(3f);
            }
        }
        return resolvable;
    }

    protected @NotNull ResolvableFloat getMinCreativeReachResolvable(ConfigurationSection section, String path) {
        ResolvableFloat resolvable = this.asResolvableFloat(section, "min-creative-reach");
        if (resolvable == null) return ResolvableFloat.of(0f);

        if (!resolvable.isDynamic()) {
            float value = resolvable.getResolvedValue();
            if (value > 64f || value < 0f) {
                if (Configuration.enableDebug)
                    Logger.info("Invalid min-creative-reach value in attack-range component at path: " + path + ". Value: " + value + ". It must be between 0 and 64. Using default value 0f.");
                return ResolvableFloat.of(0f);
            }
        }
        return resolvable;
    }

    protected @NotNull ResolvableFloat getMaxCreativeReachResolvable(ConfigurationSection section, String path) {
        ResolvableFloat resolvable = this.asResolvableFloat(section, "max-creative-reach");
        if (resolvable == null) return ResolvableFloat.of(5f);

        if (!resolvable.isDynamic()) {
            float value = resolvable.getResolvedValue();
            if (value > 64f || value < 0f) {
                if (Configuration.enableDebug)
                    Logger.info("Invalid max-creative-reach value in attack-range component at path: " + path + ". Value: " + value + ". It must be between 0 and 64. Using default value 5f.");
                return ResolvableFloat.of(5f);
            }
        }
        return resolvable;
    }

    protected @NotNull ResolvableFloat getHitboxMarginResolvable(ConfigurationSection section, String path) {
        ResolvableFloat resolvable = this.asResolvableFloat(section, "hitbox-margin");
        if (resolvable == null) return ResolvableFloat.of(0.3f);

        if (!resolvable.isDynamic()) {
            float value = resolvable.getResolvedValue();
            if (value < 0f || value > 1f) {
                if (Configuration.enableDebug)
                    Logger.info("Invalid hitbox-margin value in attack-range component at path: " + path + ". Value: " + value + ". It must be between 0 and 1. Using default value 0.3f.");
                return ResolvableFloat.of(0.3f);
            }
        }
        return resolvable;
    }

    protected @NotNull ResolvableFloat getMobFactorResolvable(ConfigurationSection section, String path) {
        ResolvableFloat resolvable = this.asResolvableFloat(section, "mob-factor");
        if (resolvable == null) return ResolvableFloat.of(1f);

        if (!resolvable.isDynamic()) {
            float value = resolvable.getResolvedValue();
            if (value < 0f || value > 2f) {
                if (Configuration.enableDebug)
                    Logger.info("Invalid mob-factor value in attack-range component at path: " + path + ". Value: " + value + ". It must be between 0 and 2. Using default value 1f.");
                return ResolvableFloat.of(1f);
            }
        }
        return resolvable;
    }
}
