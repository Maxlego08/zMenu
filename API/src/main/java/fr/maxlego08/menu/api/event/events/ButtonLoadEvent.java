package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.event.MenuEvent;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import dev.dejvokep.boostedyaml.YamlDocument;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

/**
 * <p>Event called when a Button will be loaded</p>
 */
@SuppressWarnings("unused")
public class ButtonLoadEvent extends MenuEvent {

    private final YamlConfiguration configuration;
    private final YamlDocument document;
    private final String path;
    private final ButtonManager buttonManager;
    private final ButtonLoader buttonLoader;
    private final Button button;

    /**
     * @deprecated use {@link #ButtonLoadEvent(YamlDocument, String, ButtonManager, ButtonLoader, Button)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    public ButtonLoadEvent(@NotNull YamlConfiguration configuration, @NotNull String path,@NotNull ButtonManager buttonManager,@NotNull ButtonLoader buttonLoader,@NotNull Button button) {
        this(configuration, YamlSections.toDocument(configuration), path, buttonManager, buttonLoader, button);
    }

    /**
     * {@link #getConfiguration()} is then a Bukkit copy of the document.
     */
    public ButtonLoadEvent(@NotNull YamlDocument document, @NotNull String path, @NotNull ButtonManager buttonManager, @NotNull ButtonLoader buttonLoader, @NotNull Button button) {
        this(YamlSections.toBukkit(document), document, path, buttonManager, buttonLoader, button);
    }

    private ButtonLoadEvent(@NotNull YamlConfiguration configuration, @NotNull YamlDocument document, @NotNull String path, @NotNull ButtonManager buttonManager, @NotNull ButtonLoader buttonLoader, @NotNull Button button) {
        this.configuration = configuration;
        this.document = document;
        this.path = path;
        this.buttonManager = buttonManager;
        this.buttonLoader = buttonLoader;
        this.button = button;
    }

    /**
     * @deprecated use {@link #getDocument()}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    @Contract(pure = true)
    @NotNull
    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    @Contract(pure = true)
    @NotNull
    public YamlDocument getDocument() {
        return this.document;
    }

    @Contract(pure = true)
    @NotNull
    public String getPath() {
        return this.path;
    }

    @Contract(pure = true)
    @NotNull
    public ButtonManager getButtonManager() {
        return this.buttonManager;
    }

    @Contract(pure = true)
    @NotNull
    public ButtonLoader getButtonLoader() {
        return this.buttonLoader;
    }

    @Contract(pure = true)
    @NotNull
    public Button getButton() {
        return this.button;
    }
}
