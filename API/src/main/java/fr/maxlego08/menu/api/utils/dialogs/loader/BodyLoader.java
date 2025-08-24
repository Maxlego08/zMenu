package fr.maxlego08.menu.api.utils.dialogs.loader;

import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.enums.DialogBodyType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public interface BodyLoader {
    String getKey();
    DialogBodyType getBodyType();
    BodyButton load(String path, File file, YamlConfiguration configuration);
}
