package fr.maxlego08.menu.pattern;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.loader.PatternLoader;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import fr.maxlego08.menu.zcore.utils.storage.Persist;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ZPatternManager implements PatternManager {

    private final MenuPlugin plugin;
    private final Map<String, Pattern> patterns = new HashMap<>();

    public ZPatternManager(MenuPlugin plugin) {
        this.plugin = plugin;
    }

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

        // Check if file exist
        File folder = new File(this.plugin.getDataFolder(), "patterns");
        if (!folder.exists()) {
            folder.mkdir();
        }

        this.patterns.clear();

        // Load inventories
        try {
            Files.walk(Paths.get(folder.getPath())).skip(1).map(Path::toFile).filter(File::isFile)
                    .filter(e -> e.getName().endsWith(".yml")).forEach(file -> {
                        try {
                            this.loadPattern(file);
                        } catch (InventoryException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Pattern loadPattern(File file) throws InventoryException {

        Loader<Pattern> loader = new PatternLoader(this.plugin);

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        Pattern pattern = loader.load(yamlConfiguration, "", file);

        if (pattern != null) {
            this.patterns.put(pattern.getName(), pattern);
        }

        return pattern;
    }
}
