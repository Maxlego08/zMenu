package fr.maxlego08.menu.api.pattern;

import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.storage.Saveable;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

public interface PatternManager extends Saveable {

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
     *
     * @param file load a pattern
     * @return pattern
     */
    Pattern loadPattern(File file) throws InventoryException;

}
