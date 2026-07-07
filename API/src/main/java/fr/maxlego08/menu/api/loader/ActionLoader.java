package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * A loader for creating instances of {@link Action} based on configuration.
 */
public abstract class ActionLoader {

    private final List<String> keys;

    /**
     * Creates an instance of {@link ActionLoader} with the specified keys.
     *
     * @param keys The keys that define the type of action.
     */
    public ActionLoader(@NotNull List<String> keys) {
        this.keys = keys;
    }

    /**
     * Creates an instance of {@link ActionLoader} with the specified keys.
     *
     * @param keys The keys that define the type of action.
     */
    public ActionLoader(@NotNull String... keys) {
        this.keys = List.of(keys);
    }

    /**
     * Gets the key that defines the type of action.
     *
     * @return keys.
     */
    @Contract(pure = true)
    @NotNull
    public List<String> getKeys() {
        return this.keys;
    }

    /**
     * Creates an instance of {@link Action} based on the provided configuration.
     *
     * @param path     The path in the configuration file.
     * @param accessor The map accessor containing the configuration elements.
     * @param file     The file where the configuration is located.
     * @return The created {@link Action}.
     */
    @Nullable
    public abstract Action load(@NotNull String path,@NotNull TypedMapAccessor accessor,@NotNull File file);
}
