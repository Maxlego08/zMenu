package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.*;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableComponent;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Function;

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

    /**
     * Builds this component from the data an item already has, to turn an item into a menu item.
     * It is only called when the item changes this component compared to the default item of its material.
     *
     * @param itemStack The item to read.
     * @return The component, or null when this loader cannot express the item's value.
     */
    @Nullable
    public ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        return null;
    }

    /**
     * Same as {@link #fromItemStack}, for servers without Paper's data component API: reads the value through
     * {@link org.bukkit.inventory.meta.ItemMeta} and returns the component that applies it the same way.
     * Every loader is asked for every item, so null also means the item does not have this setting.
     *
     * @param itemStack The item to read.
     * @return The component, or null when the item does not have this setting or it cannot be expressed.
     */
    @Nullable
    public ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        return null;
    }

    /**
     * Reads the data component named like this loader ({@code cat/collar} → {@code minecraft:cat/collar}).
     *
     * @return The value, or null when the item has none or the component carries no value.
     */
    protected @Nullable Object getNamedData(@NotNull ItemStack itemStack) {
        NamespacedKey key = NamespacedKey.minecraft(this.componentName.replace('-', '_'));
        DataComponentType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE).get(key);
        return type instanceof DataComponentType.Valued<?> valued ? itemStack.getData(valued) : null;
    }

    /**
     * @return The text with {@code §} codes, or null when they cannot express the component (fonts, events, translations…).
     */
    protected static @Nullable String toLegacyText(@NotNull Component component) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        String text = serializer.serialize(component);
        if (isPlaceholderLike(text)) return null;
        return serializer.deserialize(text).compact().equals(component.compact()) ? text : null;
    }

    /**
     * A text with two {@code %} is read back as a placeholder expression, so it would not stay the same text.
     */
    protected static boolean isPlaceholderLike(@NotNull String text) {
        return Resolvable.isExpression(text);
    }

    /**
     * @return The component, kept as is, or null when its MiniMessage text would not read back as the same component.
     */
    protected static @Nullable ResolvableComponent toResolvableComponent(@NotNull Component component, @NotNull PaperMetaUpdater metaUpdater) {
        String miniMessage = metaUpdater.getMiniMessage(component);
        if (miniMessage == null || isPlaceholderLike(miniMessage)) return null;
        if (!metaUpdater.getComponent(miniMessage).compact().equals(component.compact())) return null;
        return ResolvableComponent.ofValue(component, metaUpdater);
    }

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
            names.add(this.plugin.getName().toLowerCase(Locale.ROOT) + ":" + this.componentName);
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

    @Nullable
    protected <T> T resolveValue(ConfigurationSection configuration, String path, Function<Number, T> numberMapper, Function<String, T> stringMapper) {
        Object value = configuration.get(path);

        if (value instanceof Number number) {
            return numberMapper.apply(number);
        }

        if (value instanceof String string) {
            return stringMapper.apply(string);
        }

        return null;
    }

    @NotNull
    protected <T> T resolveValue(ConfigurationSection configuration, String path, Function<Number, T> numberMapper, Function<String, T> stringMapper, T defaultValue) {
        T value = this.resolveValue(configuration, path, numberMapper, stringMapper);
        return value != null ? value : defaultValue;
    }

    @Nullable
    protected ResolvableInt asResolvableInt(ConfigurationSection configuration, String path) {
        return this.resolveValue(
                configuration,
                path,
                number -> ResolvableInt.of(number.intValue()),
                ResolvableInt::of
        );
    }

    @NotNull
    protected ResolvableInt asResolvableInt(ConfigurationSection configuration, String path, int defaultValue) {
        return this.resolveValue(
                configuration,
                path,
                number -> ResolvableInt.of(number.intValue()),
                ResolvableInt::of,
                ResolvableInt.of(defaultValue)
        );
    }

    @Nullable
    protected ResolvableFloat asResolvableFloat(ConfigurationSection configuration, String path) {
        return this.resolveValue(
                configuration,
                path,
                number -> ResolvableFloat.of(number.floatValue()),
                ResolvableFloat::of
        );
    }

    @NotNull
    protected ResolvableFloat asResolvableFloat(ConfigurationSection configuration, String path, float defaultValue) {
        return this.resolveValue(
                configuration,
                path,
                number -> ResolvableFloat.of(number.floatValue()),
                ResolvableFloat::of,
                ResolvableFloat.of(defaultValue)
        );
    }

    @Nullable
    protected ResolvableDouble asResolvableDouble(ConfigurationSection configuration, String path) {
        return this.resolveValue(
                configuration,
                path,
                number -> ResolvableDouble.of(number.doubleValue()),
                ResolvableDouble::of
        );
    }

    @NotNull
    protected ResolvableDouble asResolvableDouble(ConfigurationSection configuration, String path, double defaultValue) {
        return this.resolveValue(
                configuration,
                path,
                number -> ResolvableDouble.of(number.doubleValue()),
                ResolvableDouble::of,
                ResolvableDouble.of(defaultValue)
        );
    }

    @Nullable
    protected ResolvableLong asResolvableLong(ConfigurationSection configuration, String path) {
        return this.resolveValue(
                configuration,
                path,
                number -> ResolvableLong.of(number.longValue()),
                ResolvableLong::of
        );
    }

    @NotNull
    protected ResolvableLong asResolvableLong(ConfigurationSection configuration, String path, long defaultValue) {
        return this.resolveValue(
                configuration,
                path,
                number -> ResolvableLong.of(number.longValue()),
                ResolvableLong::of,
                ResolvableLong.of(defaultValue)
        );
    }

    @Nullable
    protected ResolvableBoolean asResolvableBoolean(ConfigurationSection configuration, String path) {
        Object value = configuration.get(path);

        if (value instanceof Boolean bool) {
            return ResolvableBoolean.of(bool);
        }

        if (value instanceof String expression) {
            return ResolvableBoolean.of(expression);
        }

        return null;
    }

    @NotNull
    protected ResolvableBoolean asResolvableBoolean(ConfigurationSection configuration, String path, boolean defaultValue) {
        ResolvableBoolean value = this.asResolvableBoolean(configuration, path);
        return value != null ? value : ResolvableBoolean.of(defaultValue);
    }

    @Nullable
    protected ResolvableSound asResolvableSound(ConfigurationSection configuration, String path) {
        Object value = configuration.get(path);

        if (value instanceof String expression) {
            Optional<Sound> sound = this.getSound(expression);
            return sound.map(ResolvableSound::of).orElseGet(() -> ResolvableSound.of(expression));
        }

        return null;
    }

    @NotNull
    protected ResolvableSound asResolvableSound(ConfigurationSection configuration, String path, Sound defaultValue) {
        ResolvableSound value = this.asResolvableSound(configuration, path);
        return value != null ? value : ResolvableSound.of(defaultValue);
    }

    @Nullable
    protected ResolvableNamespacedKey asResolvableKey(ConfigurationSection configuration, String path) {
        Object value = configuration.get(path);

        if (value instanceof String expression) {
            Optional<NamespacedKey> key = Optional.ofNullable(NamespacedKey.fromString(expression));
            return key.map(ResolvableNamespacedKey::of).orElseGet(() -> ResolvableNamespacedKey.of(expression));
        }

        return null;
    }

    @NotNull
    protected ResolvableNamespacedKey asResolvableKey(ConfigurationSection configuration, String path, NamespacedKey defaultValue) {
        ResolvableNamespacedKey value = this.asResolvableKey(configuration, path);
        return value != null ? value : ResolvableNamespacedKey.of(defaultValue);
    }


    @Nullable
    protected <E extends Enum<E>> ResolvableEnum<E> asResolvableEnum(ConfigurationSection configuration, String path, Class<E> enumClass
    ) {
        Object value = configuration.get(path);

        if (value instanceof String string) {
            return ResolvableEnum.auto(enumClass, string);
        }

        return null;
    }

    @NotNull
    protected <E extends Enum<E>> ResolvableEnum<E> asResolvableEnum(ConfigurationSection configuration, String path, Class<E> enumClass, E defaultValue) {
        ResolvableEnum<E> value = this.asResolvableEnum(configuration, path, enumClass);

        return value != null
                ? value
                : ResolvableEnum.of(enumClass, defaultValue);
    }

    protected Optional<Sound> getSound(@Nullable String soundName) {
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
