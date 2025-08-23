package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.InputLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class TextInputLoader implements InputLoader {
    @Override
    public String getKey() {
        return "text";
    }

    @Override
    public DialogInputType getInputType() {
        return DialogInputType.TEXT;
    }

    @Override
    public InputButton load(String path, File file, YamlConfiguration configuration) {
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

        return inputButton;
    }
}
