package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.event.MenuEvent;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * <p>Event called when a Button will be loaded</p>
 */
public class ButtonLoadEvent extends MenuEvent {

    private final YamlConfiguration configuration;
    private final String path;
    private final ButtonManager buttonManager;
    private final ButtonLoader buttonLoader;
    private final Button button;

    public ButtonLoadEvent(YamlConfiguration configuration, String path, ButtonManager buttonManager, ButtonLoader buttonLoader, Button button) {
        this.configuration = configuration;
        this.path = path;
        this.buttonManager = buttonManager;
        this.buttonLoader = buttonLoader;
        this.button = button;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public String getPath() {
        return path;
    }

    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    public ButtonLoader getButtonLoader() {
        return buttonLoader;
    }

    public Button getButton() {
        return button;
    }
}
