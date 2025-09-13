package fr.maxlego08.menu.hooks.dialogs.button.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.dialogs.button.buttons.ZDialogItemBody;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class DialogItemBodyLoader extends ButtonLoader {

    public DialogItemBodyLoader(Plugin plugin) {
        super(plugin, "dialog_item");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        int width = configuration.getInt(path+".width", 128);
        int height = configuration.getInt(path+".height", 128);
        boolean showDecoration = configuration.getBoolean(path+".show-decoration", true);
        boolean showTooltip = configuration.getBoolean(path+".show-tooltip", true);
        List<String> descriptionMessages = configuration.getStringList(path+".description.messages");
        int descriptionWidth = configuration.getInt(path+".description.width", 512);
        boolean useCache = configuration.getBoolean(path+".use-cache", true);

        return new ZDialogItemBody(descriptionMessages, descriptionWidth, showDecoration, showTooltip, useCache, width, height);
    }
}
