package fr.maxlego08.menu.zcore.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class GlobalDatabaseConfiguration {

    private final FileConfiguration pluginConfiguration;
    private final FileConfiguration globalConfiguration;

    public GlobalDatabaseConfiguration(FileConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
        File file = new File("database-configuration.yml");
        if (file.exists()) {
            this.globalConfiguration = YamlConfiguration.loadConfiguration(file);
        } else {
            this.globalConfiguration = null;
        }
    }

    /**
     * Retrieves the database host from the configuration.
     * If a global configuration file is present, this method will first check it for the host.
     * If the global configuration file is not available, it will fall back to the plugin configuration.
     * If no host is specified in either configuration, the default value is "192.168.10.10".
     *
     * @return the database host
     */
    public String getHost() {
        return this.globalConfiguration == null ? this.pluginConfiguration.getString("database-configuration.host") : this.globalConfiguration.getString("database-configuration.host", this.pluginConfiguration.getString("database-configuration.host", "192.168.10.10"));
    }

    /**
     * Retrieves the database port from the configuration.
     * If a global configuration file is present, this method will first check it for the port.
     * If the global configuration file is not available, it will fall back to the plugin configuration.
     * If no port is specified in either configuration, the default value is 3306.
     *
     * @return the database port
     */
    public int getPort() {
        return this.globalConfiguration == null ? this.pluginConfiguration.getInt("database-configuration.port") : this.globalConfiguration.getInt("database-configuration.port", this.pluginConfiguration.getInt("database-configuration.port", 3306));
    }

    /**
     * Retrieves the database name from the configuration.
     * If a global configuration file is present, this method will first check it for the database name.
     * If the global configuration file is not available, it will fall back to the plugin configuration.
     * If no database name is specified in either configuration, the default value is "homestead".
     *
     * @return the name of the database
     */
    public String getDatabase() {
        return this.globalConfiguration == null ? this.pluginConfiguration.getString("database-configuration.database") : this.globalConfiguration.getString("database-configuration.database", this.pluginConfiguration.getString("database-configuration.database", "homestead"));
    }

    /**
     * Retrieves the database user from the configuration.
     * If a global configuration file is present, this method will first check it for the user.
     * If the global configuration file is not available, it will fall back to the plugin configuration.
     * If no user is specified in either configuration, the default value is "homestead".
     *
     * @return the database user
     */
    public String getUser() {
        return this.globalConfiguration == null ? this.pluginConfiguration.getString("database-configuration.user") : this.globalConfiguration.getString("database-configuration.user", this.pluginConfiguration.getString("database-configuration.user", "homestead"));
    }

    /**
     * Retrieves the database password from the configuration.
     * If a global configuration file is present, this method will first check it for the password.
     * If the global configuration file is not available, it will fall back to the plugin configuration.
     * If no password is specified in either configuration, the default value is "secret".
     *
     * @return the database password
     */
    public String getPassword() {
        return this.globalConfiguration == null ? this.pluginConfiguration.getString("database-configuration.password") : this.globalConfiguration.getString("database-configuration.password", this.pluginConfiguration.getString("database-configuration.password", "secret"));
    }

    /**
     * Retrieves the table prefix from the database configuration.
     * This method first checks the global configuration file for the table prefix.
     * If the global configuration file is not available, it falls back to the plugin configuration.
     * The default value is "groupez_" if no prefix is specified in either configuration.
     *
     * @return the table prefix for database tables
     */
    public String getTablePrefix() {
        return this.globalConfiguration == null ? this.pluginConfiguration.getString("database-configuration.table-prefix") : this.globalConfiguration.getString("database-configuration.table-prefix", this.pluginConfiguration.getString("database-configuration.table-prefix", "groupez_"));
    }

    /**
     * Determines if debugging is enabled in the database configuration.
     * This checks the 'debug' setting from the global configuration file if it exists,
     * otherwise falls back to the plugin configuration.
     *
     * @return true if debugging is enabled, false otherwise
     */
    public boolean isDebug() {
        return this.globalConfiguration == null ? this.pluginConfiguration.getBoolean("database-configuration.debug") : this.globalConfiguration.getBoolean("database-configuration.debug", this.pluginConfiguration.getBoolean("database-configuration.debug", false));
    }
}
