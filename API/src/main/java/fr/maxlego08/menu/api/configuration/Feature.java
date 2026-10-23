package fr.maxlego08.menu.api.configuration;

import org.jetbrains.annotations.NotNull;

/**
 * An optional part of zMenu that a server can turn off in the {@code features} section of
 * {@code config.yml}. A disabled feature registers nothing: no listener, command, registry entry or
 * default file.
 * <p>
 * The state is read once when zMenu enables; changing it needs a server restart.
 *
 * @see fr.maxlego08.menu.api.MenuPlugin#isFeatureEnabled(Feature)
 */
public enum Feature {

    /**
     * Custom items from the {@code items} folder, their mechanics and the {@code /zm giveitem} command.
     * When disabled, {@link fr.maxlego08.menu.api.MenuPlugin#getItemManager()} returns a manager that
     * knows no item and ignores every registration.
     */
    CUSTOM_ITEMS("custom-items", true);

    private final String path;
    private final boolean enabledByDefault;

    Feature(String path, boolean enabledByDefault) {
        this.path = path;
        this.enabledByDefault = enabledByDefault;
    }

    /**
     * @return the key of this feature in the {@code features} section of {@code config.yml}
     */
    @NotNull
    public String getPath() {
        return this.path;
    }

    /**
     * @return whether this feature is enabled when {@code config.yml} does not mention it
     */
    public boolean isEnabledByDefault() {
        return this.enabledByDefault;
    }
}
