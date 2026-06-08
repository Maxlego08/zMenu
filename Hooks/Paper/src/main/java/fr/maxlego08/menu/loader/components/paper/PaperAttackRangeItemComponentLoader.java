package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.paper.PaperAttackRangeComponent;
import fr.maxlego08.menu.loader.components.AbstractAttackRangeItemComponentLoader;
import io.papermc.paper.datacomponent.item.AttackRange;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
@PaperOnly
public class PaperAttackRangeItemComponentLoader extends AbstractAttackRangeItemComponentLoader {

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            return null;
        }

        AttackRange.Builder builder = AttackRange.attackRange();

        builder.minReach(getMinReach(componentSection, path));
        builder.maxReach(getMaxReach(componentSection, path));
        builder.minCreativeReach(getMinCreativeReach(componentSection, path));
        builder.maxCreativeReach(getMaxCreativeReach(componentSection, path));
        builder.hitboxMargin(getHitboxMargin(componentSection, path));
        builder.mobFactor(getMobFactor(componentSection, path));

        return new PaperAttackRangeComponent(builder.build());
    }
}
