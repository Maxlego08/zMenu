package fr.maxlego08.menu.api.pattern;

import fr.maxlego08.menu.exceptions.InventoryException;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

/**
 * <p>Pattern management</p>
 */
public interface PatternManager {

    /**
     * Get patterns list
     *
     * @return patterns
     */
    Collection<Pattern> getPatterns();

    /**
     * @param name Pattern name
     * @return optional
     */
    Optional<Pattern> getPattern(String name);

    /**
     * Register a pattern
     *
     * @param pattern New pattern
     */
    void registerPattern(Pattern pattern);

    /**
     * Unregister a pattern
     *
     * @param pattern old pattern
     */
    void unregisterPattern(Pattern pattern);

    /**
     * @param file load a pattern
     * @return pattern
     */
    Pattern loadPattern(File file) throws InventoryException;

    /**
     * Load patterns
     */
    void loadPatterns();

}
