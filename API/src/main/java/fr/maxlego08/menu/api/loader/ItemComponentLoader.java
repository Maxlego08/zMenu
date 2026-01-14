package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ItemComponentLoader {
    private final String componentName;

    public ItemComponentLoader(@NotNull String componentName) {
        this.componentName = componentName;
    }

    @Nullable
    public abstract ItemComponent load(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull ConfigurationSection componentSection);

    @NotNull
    public String getComponentName() {
        return this.componentName;
    }
}
