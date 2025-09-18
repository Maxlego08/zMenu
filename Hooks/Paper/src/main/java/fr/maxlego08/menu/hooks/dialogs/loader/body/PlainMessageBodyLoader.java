package fr.maxlego08.menu.hooks.dialogs.loader.body;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.enums.DialogBodyType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class PlainMessageBodyLoader extends ButtonLoader {

    public PlainMessageBodyLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public PlainMessageBodyLoader(Plugin plugin) {
        super(plugin, "plain_message");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        BodyButton bodyButton = new BodyButton();
        List<String> messages = configuration.getStringList(path+".messages");
        int width = configuration.getInt(path+".width",128);
        bodyButton.setWidth(width);
        bodyButton.setMessages(messages);
        bodyButton.setBodyType(DialogBodyType.PLAIN_MESSAGE);
        return bodyButton;
    }
}
