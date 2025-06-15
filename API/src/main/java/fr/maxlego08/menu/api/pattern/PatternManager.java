package fr.maxlego08.menu.api.pattern;

import fr.maxlego08.menu.api.exceptions.InventoryException;

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
    Collection<Pattern> getPatterns();

    /**
     * Retrieves a pattern by its name.
     *
     * @param name The name of the pattern to retrieve.
     * @return An optional containing the pattern, if it exists.
     */
    Optional<Pattern> getPattern(String name);

    /**
     * Registers a new pattern.
     *
     * @param pattern The pattern to be registered.
     */
    void registerPattern(Pattern pattern);

    /**
     * Unregisters a pattern.
     *
     * @param pattern The pattern to be unregistered.
     */
    void unregisterPattern(Pattern pattern);

    /**
     * Loads a pattern from a file.
     *
     * @param file The file from which to load the pattern.
     * @return The loaded {@link Pattern}.
     * @throws InventoryException If an error occurs while loading the pattern.
     */
    Pattern loadPattern(File file) throws InventoryException;

    /**
     * Loads all registered patterns.
     */
    void loadPatterns();

}
