package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.InteractAnimationComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableSwingAnimation;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("26.3")
public final class InteractAnimationItemComponentLoader extends ItemComponentLoader {

    public InteractAnimationItemComponentLoader() {
        super("interact-animation");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableEnum<SwingAnimation.Animation> type = ResolvableEnum.autoOrNull(SwingAnimation.Animation.class, componentSection.getString("type"));
        ResolvableInt duration = ResolvableInt.autoOrNull(componentSection.getString("duration"));

        return new InteractAnimationComponent(new ResolvableSwingAnimation(type, duration));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        SwingAnimation animation = itemStack.getData(DataComponentTypes.INTERACT_ANIMATION);
        if (animation == null) return null;
        return new InteractAnimationComponent(new ResolvableSwingAnimation(ResolvableEnum.of(SwingAnimation.Animation.class, animation.type()), ResolvableInt.of(animation.duration())));
    }
}
