package fr.maxlego08.menu.pattern;

import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.zcore.utils.storage.Persist;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ZPatternManager implements PatternManager {

    private final Map<String, Pattern> patterns = new HashMap<>();

    @Override
    public Collection<Pattern> getPatterns() {
        return patterns.values();
    }

    @Override
    public Optional<Pattern> getPattern(String name) {
        return Optional.ofNullable(patterns.getOrDefault(name, null));
    }

    @Override
    public void registerPattern(Pattern pattern) {
        patterns.put(pattern.getName(), pattern);
    }

    @Override
    public void unregisterPattern(Pattern pattern) {
        patterns.remove(pattern.getName());
    }

    @Override
    public void save(Persist persist) {

    }

    @Override
    public void load(Persist persist) {

    }
}
