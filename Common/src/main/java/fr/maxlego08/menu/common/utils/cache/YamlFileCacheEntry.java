package fr.maxlego08.menu.common.utils.cache;

import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.File;

public class YamlFileCacheEntry {
    private final long lastModified;
    private final File file;
    private final YamlDocument yamlConfiguration;

    public YamlFileCacheEntry(File file, YamlDocument yamlConfiguration) {
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

    public YamlDocument getYamlConfiguration() {
        return this.yamlConfiguration;
    }

    public long getLastModified() {
        return this.lastModified;
    }
}
