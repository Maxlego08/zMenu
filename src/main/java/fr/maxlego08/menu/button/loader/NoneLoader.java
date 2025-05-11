package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class NoneLoader extends ButtonLoader {

    private final Class<? extends Button> classz;

    public NoneLoader(Plugin plugin, Class<? extends Button> classz, String name) {
        super(plugin, name);
        this.classz = classz;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        try {
            return this.classz.getConstructor(Plugin.class).newInstance(this.plugin);
        } catch (Exception exception) {
            try {
                return this.classz.newInstance();
            } catch (Exception exception2) {
                exception.printStackTrace();
            }
        }
        return null;
    }

}
