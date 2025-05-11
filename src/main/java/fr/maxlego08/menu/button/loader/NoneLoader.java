package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.button.Button;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class NoneLoader implements ButtonLoader {

    private final Plugin plugin;
    private final Class<? extends Button> classz;
    private final String name;

    /**
     * @param plugin
     */
    public NoneLoader(Plugin plugin, Class<? extends Button> classz, String name) {
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
            return this.classz.getConstructor(Plugin.class).newInstance(this.plugin);
        } catch (Exception e) {
            try {
                return this.classz.newInstance();
            } catch (Exception e2) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
