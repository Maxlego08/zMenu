package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public abstract class ItemComponentLoader {
    public enum ComponentOwnerType {
        /**
         * Component provided by Minecraft itself.
         */
        MINECRAFT,
        /**
         * Component provided and managed by a plugin.
         */
        PLUGIN
    }

    private final String componentName;
    private final ComponentOwnerType ownerType;
    @Nullable
    private final Plugin plugin;

    public ItemComponentLoader(@NotNull String componentName) {
        this(componentName, ComponentOwnerType.MINECRAFT, null);
    }

    /**
     * Constructor for plugin-owned components.
     * @param componentName The name of the component
     * @param plugin The plugin that owns this component
     */
    public ItemComponentLoader(@NotNull String componentName, @NotNull Plugin plugin) {
        this(componentName, ComponentOwnerType.PLUGIN, plugin);
    }

    /**
     * General constructor for ItemComponentLoader.
     * @param componentName The name of the component
     * @param ownerType The owner type of the component
     * @param plugin The plugin that owns this component, can be null if ownerType is MINECRAFT
     * @throws IllegalArgumentException if ownerType is PLUGIN and plugin is null
     */
    public ItemComponentLoader(@NotNull String componentName, @NotNull ComponentOwnerType ownerType, @Nullable Plugin plugin) throws IllegalArgumentException {
        if (ownerType == ComponentOwnerType.PLUGIN && plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null for PLUGIN owned components.");
        }
        this.componentName = componentName;
        this.ownerType = ownerType;
        this.plugin = plugin;
    }

    @Nullable
    public abstract ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection);

    protected String normalizePath(@NotNull String path) {
        if (path.endsWith(".")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * Get all possible names for this component, including namespaced versions.
     * E.g., for Minecraft components, both "name" and "minecraft:name" are returned.
     * For plugin components, "pluginname:name" is returned.
     * @return An unmodifiable list of component names
     */
    @NotNull
    public List<String> getComponentNames() {
        List<String> names = new ArrayList<>();
        if (this.ownerType == ComponentOwnerType.MINECRAFT) {
            names.add(this.componentName);
            names.add("minecraft:" + this.componentName);
        } else if (this.ownerType == ComponentOwnerType.PLUGIN && this.plugin != null) {
            names.add(this.plugin.getName().toLowerCase() + ":" + this.componentName);
        }
        return Collections.unmodifiableList(names);
    }

    @NotNull
    public String getComponentName() {
        return this.componentName;
    }

    @NotNull
    public ComponentOwnerType getOwnerType() {
        return this.ownerType;
    }

    @Nullable
    public Plugin getPlugin() {
        return this.plugin;
    }


    //**************\\
    // Utils methods ||
    //**************//

    protected Optional<Sound> getSound(@Nullable String soundName){
        if (soundName == null || soundName.isBlank()) {
            return Optional.empty();
        }
        try {
            NamespacedKey key = NamespacedKey.fromString(soundName);
            if (key != null) {
                return Optional.of(Registry.SOUNDS.getOrThrow(key));
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }
}
