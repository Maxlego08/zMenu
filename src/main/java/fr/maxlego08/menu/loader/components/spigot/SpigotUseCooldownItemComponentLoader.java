package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.UseCooldownComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotUseCooldownItemComponentLoader extends ItemComponentLoader {

    public SpigotUseCooldownItemComponentLoader(){
        super("use_cooldown");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        float cooldownSeconds = (float) componentSection.getDouble("seconds", 0);
        String cooldownGroup = componentSection.getString("cooldown_group");
        NamespacedKey key = null;
        if (cooldownGroup != null) {
            key = NamespacedKey.fromString(cooldownGroup);
        }
        return new UseCooldownComponent(cooldownSeconds, key);
    }
}
