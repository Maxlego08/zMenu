package fr.maxlego08.menu.hooks.bedrock.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.utils.Loader;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public abstract class BedrockDynamicAbstractLoader extends ButtonLoader {

    protected final MenuPlugin plugin;

    public BedrockDynamicAbstractLoader(@NotNull MenuPlugin plugin, @NotNull String name) {
        super(plugin, name);
        this.plugin = plugin;
    }

    @Override
    public @Nullable Button load(@NotNull YamlDocument configuration, @NotNull String path, @NotNull DefaultButtonValue defaultButtonValue) {
        String start = configuration.getString(path + "start");
        String end = configuration.getString(path + "end");

        Loader<Button> loaderButton = this.plugin.getButtonManager().getLoaderButton(this.plugin, defaultButtonValue.getFile(), 1, Collections.emptyMap());
        try {
            String cleanPath = path.replaceAll("\\.+$", "");

            String buttonName = cleanPath.contains(".")
                    ? cleanPath.substring(cleanPath.lastIndexOf('.') + 1)
                    : cleanPath;
            Button button = loaderButton.load(configuration, path + this.getChildPath(), buttonName, defaultButtonValue);
            return this.wrap(button, buttonName, start, end);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, defaultButtonValue);
    }

    protected abstract String getChildPath();

    @Nullable
    protected abstract Button wrap(Button button, String buttonName, String start, String end);

    public static int stringToInt(MenuPlugin plugin, String str, @NotNull Player player) {
        try {
            return Integer.parseInt(plugin.parse(player, str));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
