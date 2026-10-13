package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.AttackAnimationComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableSwingAnimation;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Minecraft 26.3 renamed {@code swing_animation} to {@code attack_animation}: the old name is still read
 * here, with a warning.
 */
@AutoComponentLoader
@SinceVersion("26.3")
public final class AttackAnimationItemComponentLoader extends ItemComponentLoader {

    private static final String LEGACY_NAME = "swing-animation";

    public AttackAnimationItemComponentLoader() {
        super("attack-animation");
    }

    @Override
    public @NotNull List<String> getComponentNames() {
        List<String> names = new ArrayList<>(super.getComponentNames());
        names.add(LEGACY_NAME);
        names.add("minecraft:" + LEGACY_NAME);
        return Collections.unmodifiableList(names);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        if (this.normalizePath(path).endsWith(LEGACY_NAME)) {
            Logger.info("The component " + LEGACY_NAME + " at " + path + " in " + file.getName() + " is named attack-animation since Minecraft 26.3, use attack-animation instead.", Logger.LogType.WARNING);
        }

        ResolvableEnum<SwingAnimation.Animation> type = ResolvableEnum.autoOrNull(SwingAnimation.Animation.class, componentSection.getString("type"));
        ResolvableInt duration = ResolvableInt.autoOrNull(componentSection.getString("duration"));

        return new AttackAnimationComponent(new ResolvableSwingAnimation(type, duration));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        SwingAnimation animation = itemStack.getData(DataComponentTypes.ATTACK_ANIMATION);
        if (animation == null) return null;
        return new AttackAnimationComponent(new ResolvableSwingAnimation(ResolvableEnum.of(SwingAnimation.Animation.class, animation.type()), ResolvableInt.of(animation.duration())));
    }
}
