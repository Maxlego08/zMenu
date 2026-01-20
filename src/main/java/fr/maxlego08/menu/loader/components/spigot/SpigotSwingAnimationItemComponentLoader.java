package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.components.SwingAnimationComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotSwingAnimationItemComponentLoader extends ItemComponentLoader {

    public SpigotSwingAnimationItemComponentLoader(){
        super("swing_animation");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        int duration = componentSection.getInt("duration", 6);
        SwingAnimationComponent.Type swingAnimationType = SwingAnimationComponent.Type.WHACK;
        String typeString = componentSection.getString("type", "WHACK");
        try {
            swingAnimationType = SwingAnimationComponent.Type.valueOf(typeString.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        return new fr.maxlego08.menu.api.itemstack.components.SwingAnimationComponent(duration, swingAnimationType);
    }
}
