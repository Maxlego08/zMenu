package fr.maxlego08.menu.hooks.dialogs.utils.loader;

import fr.maxlego08.menu.hooks.dialogs.buttons.BodyButton;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogBodyType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public interface BodyLoader {
    String getKey();
    DialogBodyType getBodyType();
    BodyButton load(String path, File file, YamlConfiguration configuration);
}
