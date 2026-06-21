package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.components.SwingAnimationComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
public class SpigotSwingAnimationItemComponentLoader extends ItemComponentLoader {

    public SpigotSwingAnimationItemComponentLoader(){
        super("swing-animation");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        ResolvableInt duration = this.asResolvableInt(componentSection, "duration", 6);
        ResolvableEnum<SwingAnimationComponent.Type> swingAnimationType = this.asResolvableEnum(componentSection, "type", SwingAnimationComponent.Type.class, SwingAnimationComponent.Type.WHACK);

        return new fr.maxlego08.menu.api.itemstack.components.SwingAnimationComponent(duration, swingAnimationType);
    }
}
