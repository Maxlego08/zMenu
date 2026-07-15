package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.AttackRangeComponent;
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
    public @Nullable ItemComponent load(
            @NotNull MenuItemStackContext context,
            @NotNull File file,
            @NotNull YamlConfiguration configuration,
            @NotNull String path,
            @Nullable ConfigurationSection componentSection
    ) {

        if (componentSection == null) {
            return null;
        }

        return new AttackRangeComponent(
                this.getMinReachResolvable(componentSection, path),
                this.getMaxReachResolvable(componentSection, path),
                this.getMinCreativeReachResolvable(componentSection, path),
                this.getMaxCreativeReachResolvable(componentSection, path),
                this.getHitboxMarginResolvable(componentSection, path),
                this.getMobFactorResolvable(componentSection, path)
        );
    }
}
