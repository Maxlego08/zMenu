package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.event.MenuEvent;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Event called when a Button will be loaded</p>
 */
@SuppressWarnings("unused")
public class ButtonLoadEvent extends MenuEvent {

    private final YamlConfiguration configuration;
    private final String path;
    private final ButtonManager buttonManager;
    private final ButtonLoader buttonLoader;
    private final Button button;

    public ButtonLoadEvent(@NotNull YamlConfiguration configuration, @NotNull String path,@NotNull ButtonManager buttonManager,@NotNull ButtonLoader buttonLoader,@NotNull Button button) {
        this.configuration = configuration;
        this.path = path;
        this.buttonManager = buttonManager;
        this.buttonLoader = buttonLoader;
        this.button = button;
    }

    @Contract(pure = true)
    @NotNull
    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    @Contract(pure = true)
    @NotNull
    public String getPath() {
        return path;
    }

    @Contract(pure = true)
    @NotNull
    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    @Contract(pure = true)
    @NotNull
    public ButtonLoader getButtonLoader() {
        return buttonLoader;
    }

    @Contract(pure = true)
    @NotNull
    public Button getButton() {
        return button;
    }
}
