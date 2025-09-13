package fr.maxlego08.menu.hooks.dialogs.button.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class DialogTextInputLoader extends ButtonLoader {

    public DialogTextInputLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public DialogTextInputLoader(Plugin plugin) {
        super(plugin, "dialog_text");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        InputButton inputButton = new InputButton();
        String label = configuration.getString(path + ".label", "");
        int width = configuration.getInt(path + ".width",200);
        boolean labelVisible = configuration.getBoolean(path + ".label-visible", true);
        String defaultValue = configuration.getString(path + ".default-value", "");
        int maxLength = configuration.getInt(path + ".max-length", 32);
        int multilineMaxLines = configuration.getInt(path + ".multiline.max-lines");
        int multilineHeight = configuration.getInt(path + ".multiline.height", 20);

        inputButton.setLabel(label);
        inputButton.setWidth(width);
        inputButton.setLabelVisible(labelVisible);
        inputButton.setDefaultText(defaultValue);
        inputButton.setMaxLength(maxLength);
        if (multilineMaxLines > 0) {
            inputButton.setMultilineMaxLines(multilineMaxLines);
        }
        if (multilineHeight > 0) {
            inputButton.setMultilineHeight(multilineHeight);
        }
        inputButton.setInputType(DialogInputType.TEXT);
        return inputButton;
    }
}
