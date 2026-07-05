package fr.maxlego08.menu;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;

public class ZMenuBootstrapper implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {

    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        try {
            Class<?> aClass = Class.forName("fr.maxlego08.menu.ZMenuPlugin");
            return (JavaPlugin) aClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create plugin instance", e);
        }
    }
}
