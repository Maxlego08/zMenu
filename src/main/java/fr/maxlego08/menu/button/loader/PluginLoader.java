package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.buttons.ZButton;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PluginLoader implements ButtonLoader {

    private final Plugin plugin;
    private final Class<? extends ZButton> classz;
    private final String name;

    /**
     * @param plugin
     */
    public PluginLoader(Plugin plugin, Class<? extends ZButton> classz, String name) {
        super();
        this.plugin = plugin;
        this.classz = classz;
        this.name = name;
    }

    @Override
    public Class<? extends Button> getButton() {
        return this.classz;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        try {
            Constructor<? extends ZButton> constructor = this.classz.getConstructor(Plugin.class);
            return constructor.newInstance(this.plugin);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException |
                 IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
