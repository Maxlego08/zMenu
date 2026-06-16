package fr.maxlego08.menu.itemstack.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.annotations.SpigotOnly;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.loader.components.AbstractAttackRangeItemComponentLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
@SpigotOnly
public class SpigotAttackRangeItemComponentLoader extends AbstractAttackRangeItemComponentLoader {

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            return null;
        }
        float minReach = getMinReach(componentSection, path);
        float maxReach = getMaxReach(componentSection, path);
        float minCreativeReach = getMinCreativeReach(componentSection, path);
        float maxCreativeReach = getMaxCreativeReach(componentSection, path);
        float hitboxMargin = getHitboxMargin(componentSection, path);
        float mobFactor = getMobFactor(componentSection, path);
        return new SpigotAttackRangeComponent(
                minReach,
                maxReach,
                minCreativeReach,
                maxCreativeReach,
                hitboxMargin,
                mobFactor
        );
    }
}
