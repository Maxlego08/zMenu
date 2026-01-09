package fr.maxlego08.menu.api.pattern;

import fr.maxlego08.menu.api.exceptions.InventoryException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

/**
 * <p>The PatternManager interface provides methods for managing patterns used in inventories.</p>
 */
public interface PatternManager {

    /**
     * Retrieves the list of registered patterns.
     *
     * @return The collection of registered patterns.
     */
    @NotNull
    Collection<Pattern> getPatterns();

    @NotNull
    Collection<ActionPattern> getActionPatterns();

    /**
     * Retrieves a pattern by its name.
     *
     * @param name The name of the pattern to retrieve.
     * @return An optional containing the pattern, if it exists.
     */
    @NotNull
    Optional<Pattern> getPattern(String name);

    @NotNull
    Optional<ActionPattern> getActionPattern(String name);

    /**
     * Registers a new pattern.
     *
     * @param pattern The pattern to be registered.
     */
    void registerPattern(@NotNull Pattern pattern);

    void registerActionPattern(@NotNull ActionPattern pattern);

    /**
     * Unregisters a pattern.
     *
     * @param pattern The pattern to be unregistered.
     */
    void unregisterPattern(@NotNull Pattern pattern);

    void unregisterActionPattern(@NotNull ActionPattern pattern);

    /**
     * Loads a pattern from a file.
     *
     * @param file The file from which to load the pattern.
     * @return The loaded {@link Pattern}.
     * @throws InventoryException If an error occurs while loading the pattern.
     */
    @Nullable
    Pattern loadPattern(@NotNull File file) throws InventoryException;

    @Nullable
    ActionPattern loadActionPattern(@NotNull File file) throws InventoryException;

    /**
     * Loads all registered patterns.
     */
    void loadPatterns();

    /**
     * Loads all registered action patterns.
     */
    void loadActionsPatterns();

}
