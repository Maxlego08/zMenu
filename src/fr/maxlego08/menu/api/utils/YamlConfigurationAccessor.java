package fr.maxlego08.menu.api.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;
import java.util.List;

public class YamlConfigurationAccessor implements MapConfiguration {

    private final YamlConfiguration yamlConfig;
    private final String path;

    public YamlConfigurationAccessor(YamlConfiguration yamlConfig, String path) {
        this.yamlConfig = yamlConfig;
        this.path = path;
    }

    private String getPath(String key) {
        return path + "." + key;
    }

    @Override
    public String getString(String key) {
        return yamlConfig.getString(getPath(key));
    }

    @Override
    public String getString(String key, String defaultValue) {
        return yamlConfig.getString(getPath(key), defaultValue);
    }

    @Override
    public int getInt(String key) {
        return yamlConfig.getInt(getPath(key));
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return yamlConfig.getInt(getPath(key), defaultValue);
    }

    @Override
    public long getLong(String key) {
        return yamlConfig.getLong(getPath(key));
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return yamlConfig.getLong(getPath(key), defaultValue);
    }

    @Override
    public List<String> getStringList(String key) {
        return yamlConfig.getStringList(getPath(key));
    }

    @Override
    public List<String> getStringList(String key, List<String> defaultValue) {
        List<String> list = yamlConfig.getStringList(getPath(key));
        return list != null ? list : defaultValue;
    }

    @Override
    public List<Integer> getIntList(String key) {
        ConfigurationSection section = yamlConfig.getConfigurationSection(getPath(key));
        if (section != null) {
            return section.getIntegerList(getPath(key));
        }
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getIntList(String key, List<Integer> defaultValue) {
        List<Integer> list = getIntList(key);
        return !list.isEmpty() ? list : defaultValue;
    }

    @Override
    public boolean getBoolean(String key) {
        return yamlConfig.getBoolean(getPath(key));
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return yamlConfig.getBoolean(getPath(key), defaultValue);
    }

    @Override
    public double getDouble(String key) {
        return yamlConfig.getDouble(getPath(key));
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return yamlConfig.getDouble(getPath(key), defaultValue);
    }

    @Override
    public Object getObject(String key) {
        return yamlConfig.get(getPath(key));
    }

    @Override
    public Object getObject(String key, Object defaultValue) {
        return yamlConfig.get(getPath(key), defaultValue);
    }

    @Override
    public float getFloat(String key) {
        return (float) yamlConfig.getDouble(getPath(key));
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return (float) yamlConfig.getDouble(getPath(key), defaultValue);
    }

    @Override
    public List<?> getList(String key) {
        return yamlConfig.getList(getPath(key));
    }

    @Override
    public boolean contains(String key) {
        return yamlConfig.contains(getPath(key));
    }
}
