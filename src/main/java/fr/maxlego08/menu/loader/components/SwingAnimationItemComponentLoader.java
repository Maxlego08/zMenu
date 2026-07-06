package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.SwingAnimationComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableSwingAnimation;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
public final class SwingAnimationItemComponentLoader extends ItemComponentLoader {

    public SwingAnimationItemComponentLoader() {
        super("swing-animation");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableEnum<SwingAnimation.Animation> type = ResolvableEnum.autoOrNull(SwingAnimation.Animation.class, componentSection.getString("type"));
        ResolvableInt duration = ResolvableInt.autoOrNull(componentSection.getString("duration"));

        return new SwingAnimationComponent(new ResolvableSwingAnimation(type, duration));
    }
}
