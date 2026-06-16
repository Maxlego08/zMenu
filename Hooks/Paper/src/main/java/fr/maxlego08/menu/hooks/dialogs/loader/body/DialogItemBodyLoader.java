package fr.maxlego08.menu.hooks.dialogs.loader.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.dialogs.button.buttons.ZDialogItemBody;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.List;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogItemBodyLoader extends ButtonLoader {

    public DialogItemBodyLoader(MenuPlugin plugin) {
        super(plugin, "dialog_item", "item");
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
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
