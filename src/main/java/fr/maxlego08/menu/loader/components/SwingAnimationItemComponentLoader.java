package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.annotations.UntilVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.SwingAnimationComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableSwingAnimation;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
@SinceVersion("1.21.11")
@UntilVersion("26.2")
public final class SwingAnimationItemComponentLoader extends ItemComponentLoader {

    public SwingAnimationItemComponentLoader() {
        super("swing-animation");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;

        ResolvableEnum<SwingAnimation.Animation> type = ResolvableEnum.autoOrNull(SwingAnimation.Animation.class, componentSection.getString("type"));
        ResolvableInt duration = ResolvableInt.autoOrNull(componentSection.getString("duration"));

        return new SwingAnimationComponent(new ResolvableSwingAnimation(type, duration));
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        SwingAnimation swingAnimation = itemStack.getData(DataComponentTypes.SWING_ANIMATION);
        if (swingAnimation == null) return null;
        return new SwingAnimationComponent(new ResolvableSwingAnimation(ResolvableEnum.of(SwingAnimation.Animation.class, swingAnimation.type()), ResolvableInt.of(swingAnimation.duration())));
    }
}
