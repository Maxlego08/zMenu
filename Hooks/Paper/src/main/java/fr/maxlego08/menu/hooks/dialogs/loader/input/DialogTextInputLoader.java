package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.dialogs.inputs.DialogTextInput;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogTextInputLoader extends ButtonLoader {

    public DialogTextInputLoader(MenuPlugin plugin) {
        super(plugin, "dialog_text");
    }

    @Override
    public Button load(@NonNull YamlDocument configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String label = configuration.getString(path + ".label", "");
        int width = configuration.getInt(path + ".width",200);
        boolean labelVisible = configuration.getBoolean(path + ".label-visible", true);
        String defaultValue = configuration.getString(path + ".default-value", "");
        int maxLength = configuration.getInt(path + ".max-length", 32);
        int multilineMaxLines = configuration.getInt(path + ".multiline.max-lines");
        int multilineHeight = configuration.getInt(path + ".multiline.height", 20);

        return new DialogTextInput(label, labelVisible, defaultValue, width, maxLength, multilineMaxLines, multilineHeight);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, defaultButtonValue);
    }
}
