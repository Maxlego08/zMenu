package fr.maxlego08.menu.api.animation;

import fr.maxlego08.menu.api.utils.Loader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class TitleAnimationLoader implements Loader<TitleAnimation> {
    protected List<String> titles = new ArrayList<>();
    protected TimeUnit timeUnit = TimeUnit.SECONDS;
    protected int initialDelay = 20;
    protected int interval = 20;
    protected int cycles = -1;
    protected boolean showItemsAfterAnimation = false;
    protected int itemUpdateInterval = 1;

    protected TitleAnimationSettings loadSettings(@NotNull YamlConfiguration configuration, @NotNull String path){
        this.titles = configuration.getStringList(path + "titles");
        this.cycles = configuration.getInt(path+"cycles",-1);
        this.initialDelay = configuration.getInt(path+"initial-delay",20);
        this.interval = configuration.getInt(path+"interval",20);
        this.showItemsAfterAnimation = configuration.getBoolean(path + "show-items-after-animation", false);
        this.itemUpdateInterval = configuration.getInt(path + "item-update-interval", 1);
        try {
            this.timeUnit = TimeUnit.valueOf(configuration.getString(path + "time-unit", "SECONDS").toUpperCase());
        } catch (IllegalArgumentException e) {
            this.timeUnit = TimeUnit.SECONDS;
        }
        return new TitleAnimationSettings(this.titles,this.cycles, this.initialDelay, this.interval, this.timeUnit, this.showItemsAfterAnimation, this.itemUpdateInterval);
    }

    @Override
    public void save(TitleAnimation object, @NotNull YamlConfiguration configuration, @NotNull String path, File file, Object... objects) {
    }
}
