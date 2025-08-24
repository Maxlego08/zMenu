package fr.maxlego08.menu.hooks.dialogs.loader.body;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.enums.DialogBodyType;
import fr.maxlego08.menu.api.utils.dialogs.loader.BodyLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ItemBodyLoader implements BodyLoader {

    @Override
    public String getKey() {
        return "item";
    }

    @Override
    public DialogBodyType getBodyType() {
        return DialogBodyType.ITEM;
    }

    @Override
    public BodyButton load(String path, File file, YamlConfiguration configuration) {
        BodyButton bodyButton = new BodyButton();
        ZDialogManager.loadItemStack(configuration, path, file);
        int width = configuration.getInt(path+".width", 128);
        int height = configuration.getInt(path+".height", 128);
        boolean showDecoration = configuration.getBoolean(path+".show-decoration", true);
        boolean showTooltip = configuration.getBoolean(path+".show-tooltip", true);
        List<String> descriptionMessages = configuration.getStringList(path+".description.messages");
        int descriptionWidth = configuration.getInt(path+".description.width", 512);
        MenuItemStack itemStack = ZDialogManager.loadItemStack(configuration, path+".item.", file);
        boolean useCache = configuration.getBoolean(path+".use-cache", true);
        bodyButton.setUseCache(useCache);
        bodyButton.setWidth(width);
        bodyButton.setHeight(height);
        bodyButton.setShowDecorations(showDecoration);
        bodyButton.setShowTooltip(showTooltip);
        bodyButton.setDescriptionMessages(descriptionMessages);
        bodyButton.setDescriptionWidth(descriptionWidth);
        bodyButton.setItemStack(itemStack);
        return bodyButton;
    }
}
