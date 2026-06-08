package fr.maxlego08.menu.common.utils.cache;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class YamlFileCacheEntry {
    private final long lastModified;
    private final File file;
    private final YamlConfiguration yamlConfiguration;

    public YamlFileCacheEntry(File file, YamlConfiguration yamlConfiguration) {
        this.file = file;
        this.yamlConfiguration = yamlConfiguration;
        this.lastModified = file.lastModified();
    }

    public boolean isUpToDate() {
        return this.file.exists() && this.file.lastModified() == this.lastModified;
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getYamlConfiguration() {
        return this.yamlConfiguration;
    }

    public long getLastModified() {
        return this.lastModified;
    }
}
