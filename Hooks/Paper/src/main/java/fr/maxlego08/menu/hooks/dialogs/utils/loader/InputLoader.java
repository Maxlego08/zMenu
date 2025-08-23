package fr.maxlego08.menu.hooks.dialogs.utils.loader;

import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.api.enums.DialogInputType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public interface InputLoader {
    String getKey();
    DialogInputType getInputType();
    InputButton load(String path, File file, YamlConfiguration configuration);
}
