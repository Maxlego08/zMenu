package fr.maxlego08.menu.hooks.dialogs.loader.body;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.enums.DialogBodyType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ItemBodyLoader extends ButtonLoader {

    public ItemBodyLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public ItemBodyLoader(Plugin plugin) {
        super(plugin, "item");
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        BodyButton bodyButton = new BodyButton();
        int width = configuration.getInt(path+".width", 128);
        int height = configuration.getInt(path+".height", 128);
        boolean showDecoration = configuration.getBoolean(path+".show-decoration", true);
        boolean showTooltip = configuration.getBoolean(path+".show-tooltip", true);
        List<String> descriptionMessages = configuration.getStringList(path+".description.messages");
        int descriptionWidth = configuration.getInt(path+".description.width", 512);
        boolean useCache = configuration.getBoolean(path+".use-cache", true);
        bodyButton.setUseCache(useCache);
        bodyButton.setWidth(width);
        bodyButton.setHeight(height);
        bodyButton.setShowDecorations(showDecoration);
        bodyButton.setShowTooltip(showTooltip);
        bodyButton.setDescriptionMessages(descriptionMessages);
        bodyButton.setDescriptionWidth(descriptionWidth);
        bodyButton.setBodyType(DialogBodyType.ITEM);
        return bodyButton;
    }
}
