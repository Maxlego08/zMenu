package fr.maxlego08.menu.hooks.dialogs.loader.body;

import fr.maxlego08.menu.hooks.dialogs.buttons.BodyButton;
import fr.maxlego08.menu.api.enums.DialogBodyType;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.BodyLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class PlainMessageBodyLoader implements BodyLoader {
    @Override
    public String getKey() {
        return "plain_message";
    }

    @Override
    public DialogBodyType getBodyType() {
        return DialogBodyType.PLAIN_MESSAGE;
    }

    @Override
    public BodyButton load(String path, File file, YamlConfiguration configuration) {
        BodyButton bodyButton = new BodyButton();
        List<String> messages = configuration.getStringList(path+".messages");
        int width = configuration.getInt(path+".width",128);
        bodyButton.setWidth(width);
        bodyButton.setMessages(messages);
        return bodyButton;
    }
}
