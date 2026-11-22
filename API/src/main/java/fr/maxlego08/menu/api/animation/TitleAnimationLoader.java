package fr.maxlego08.menu.api.animation;

import fr.maxlego08.menu.api.utils.Loader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import dev.dejvokep.boostedyaml.YamlDocument;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

/**
 * Loader for title animation configurations, reading settings for how animated titles should display in inventories.
 * Handles delays, intervals, cycles, item updates, and more.
 */
public abstract class TitleAnimationLoader implements Loader<TitleAnimation> {

    protected List<String> titles = new ArrayList<>();
    protected TimeUnit timeUnit = TimeUnit.SECONDS;
    protected int initialDelay = 20;
    protected int interval = 20;
    protected int cycles = -1;
    protected boolean showItemsAfterAnimation = false;
    protected int itemUpdateInterval = 1;

    /**
     * @deprecated use {@link #loadSettings(YamlDocument, String)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    @NotNull
    protected TitleAnimationSettings loadSettings(@NotNull YamlConfiguration configuration, @NotNull String path) {
        this.titles = configuration.getStringList(path + "titles");
        this.cycles = configuration.getInt(path + "cycles", -1);
        this.initialDelay = configuration.getInt(path + "initial-delay", 20);
        this.interval = configuration.getInt(path + "interval", 20);
        this.showItemsAfterAnimation = configuration.getBoolean(path + "show-items-after-animation", false);
        this.itemUpdateInterval = configuration.getInt(path + "item-update-interval", 1);
        try {
            this.timeUnit = TimeUnit.valueOf(configuration.getString(path + "time-unit", "SECONDS").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            this.timeUnit = TimeUnit.SECONDS;
        }
        return new TitleAnimationSettings(this.titles, this.cycles, this.initialDelay, this.interval, this.timeUnit, this.showItemsAfterAnimation, this.itemUpdateInterval);
    }

    @NotNull
    protected TitleAnimationSettings loadSettings(@NotNull YamlDocument configuration, @NotNull String path) {
        this.titles = YamlSections.getStringList(configuration, path + "titles");
        this.cycles = configuration.getInt(path + "cycles", -1);
        this.initialDelay = configuration.getInt(path + "initial-delay", 20);
        this.interval = configuration.getInt(path + "interval", 20);
        this.showItemsAfterAnimation = configuration.getBoolean(path + "show-items-after-animation", false);
        this.itemUpdateInterval = configuration.getInt(path + "item-update-interval", 1);
        try {
            this.timeUnit = TimeUnit.valueOf(configuration.getString(path + "time-unit", "SECONDS").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            this.timeUnit = TimeUnit.SECONDS;
        }
        return new TitleAnimationSettings(this.titles, this.cycles, this.initialDelay, this.interval, this.timeUnit, this.showItemsAfterAnimation, this.itemUpdateInterval);
    }
}
