package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.AttackRangeComponent;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class AttackRangeItemComponentLoader extends ItemComponentLoader {

    public AttackRangeItemComponentLoader(){
        super("attack_range");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            return null;
        }
        double minReach = componentSection.getDouble("min_reach", 0f);
        if (minReach>64f || minReach<0f){
            if (Configuration.enableDebug)
                Logger.info("Invalid min_reach value in attack_range component at path: " + path + ". Value: " + minReach + ". It must be between 0 and 64. Using default value 0f.");
            minReach = 0f;
        }
        double maxReach = componentSection.getDouble("max_reach", 3f);
        if (maxReach>64f || maxReach<0f){
            if (Configuration.enableDebug)
                Logger.info("Invalid max_reach value in attack_range component at path: " + path + ". Value: " + maxReach + ". It must be between 0 and 64. Using default value 3f.");
            maxReach = 3f;
        }
        double minCreativeReach = componentSection.getDouble("min_creative_reach", 0f);
        if (minCreativeReach>64f || minCreativeReach<0f){
            if (Configuration.enableDebug)
                Logger.info("Invalid min_creative_reach value in attack_range component at path: " + path + ". Value: " + minCreativeReach + ". It must be between 0 and 64. Using default value 0f.");
            minCreativeReach = 0f;
        }
        double maxCreativeReach = componentSection.getDouble("max_creative_reach", 5f);
        if (maxCreativeReach>64f || maxCreativeReach<0f){
            if (Configuration.enableDebug)
                Logger.info("Invalid max_creative_reach value in attack_range component at path: " + path + ". Value: " + maxCreativeReach + ". It must be between 0 and 64. Using default value 5f.");
            maxCreativeReach = 5f;
        }
        double hitboxMargin = componentSection.getDouble("hitbox_margin", 0.3f);
        if (hitboxMargin<0f || hitboxMargin>1f){
            if (Configuration.enableDebug)
                Logger.info("Invalid hitbox_margin value in attack_range component at path: " + path + ". Value: " + hitboxMargin + ". It must be between 0 and 1. Using default value 0.3f.");
            hitboxMargin = 0.3f;
        }
        double mobFactor = componentSection.getDouble("mob_factor", 1f);
        if (mobFactor<0f || mobFactor>2f){
            if (Configuration.enableDebug)
                Logger.info("Invalid mob_factor value in attack_range component at path: " + path + ". Value: " + mobFactor + ". It must be between 0 and 10. Using default value 1f.");
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
