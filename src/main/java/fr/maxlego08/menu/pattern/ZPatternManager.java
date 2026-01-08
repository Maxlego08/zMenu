package fr.maxlego08.menu.pattern;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.checker.InventoryRequirementType;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.pattern.ActionPattern;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.loader.ActionPatternLoader;
import fr.maxlego08.menu.loader.PatternLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ZPatternManager implements PatternManager {

    @FunctionalInterface
    private interface ThrowingFileLoader {
        void load(File file) throws InventoryException;
    }

    private final ZMenuPlugin plugin;
    private final Map<String, Pattern> patterns = new HashMap<>();
    private final Map<String, ActionPattern> actionPatterns = new HashMap<>();

    public ZPatternManager(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Collection<Pattern> getPatterns() {
        return patterns.values();
    }

    @Override
    public Collection<ActionPattern> getActionPatterns() {
        return actionPatterns.values();
    }

    @Override
    public Optional<Pattern> getPattern(String name) {
        return Optional.ofNullable(patterns.getOrDefault(name, null));
    }

    @Override
    public Optional<ActionPattern> getActionPattern(String name) {
        return Optional.ofNullable(actionPatterns.get(name));
    }

    @Override
    public void registerPattern(Pattern pattern) {
        patterns.put(pattern.name(), pattern);
    }

    @Override
    public void registerActionPattern(@NotNull ActionPattern pattern) {
        actionPatterns.put(pattern.name(), pattern);
    }

    @Override
    public void unregisterPattern(Pattern pattern) {
        patterns.remove(pattern.name());
    }

    @Override
    public void unregisterActionPattern(ActionPattern pattern) {
        actionPatterns.remove(pattern.name());
    }

    @Override
    public Pattern loadPattern(File file) throws InventoryException {

        Loader<Pattern> loader = new PatternLoader(this.plugin);

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        Pattern pattern = loader.load(yamlConfiguration, "", file);

        if (pattern != null) {
            this.patterns.put(pattern.name(), pattern);

            if (Configuration.enableInformationMessage) {
                Logger.info(file.getPath() + " loaded successfully !", Logger.LogType.INFO);
            }

            this.plugin.getInventoryManager().loadElement(InventoryRequirementType.PATTERN, pattern.name());
        }

        return pattern;
    }

    @Override
    public ActionPattern loadActionPattern(File file) throws InventoryException {
        Loader<ActionPattern> loader = new ActionPatternLoader(this.plugin);

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        ActionPattern actionPattern = loader.load(yamlConfiguration, "", file);
        if (actionPattern != null) {
            this.actionPatterns.put(actionPattern.name(), actionPattern);

            if (Configuration.enableInformationMessage) {
                Logger.info(file.getPath() + " loaded successfully !", Logger.LogType.INFO);
            }
        }

        return actionPattern;
    }

    @Override
    public void loadPatterns() {
        loadFromFolder("patterns", this::loadPattern);
    }

    @Override
    public void loadActionsPatterns() {
        loadFromFolder("actions_patterns", this::loadActionPattern);
    }

    private void loadFromFolder(String folderName, ThrowingFileLoader loader) {
        File folder = new File(this.plugin.getDataFolder(), folderName);
        if (!folder.exists() && !folder.mkdir()) {
            Logger.info("Could not create " + folderName + " folder!", Logger.LogType.ERROR);
            return;
        }

        try (Stream<Path> stream = Files.walk(Paths.get(folder.getPath()))) {
            stream.skip(1)
                .map(Path::toFile)
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith(".yml"))
                .forEach(file -> {
                    try {
                        loader.load(file);
                    } catch (InventoryException e) {
                        e.printStackTrace();
                    }
                });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

