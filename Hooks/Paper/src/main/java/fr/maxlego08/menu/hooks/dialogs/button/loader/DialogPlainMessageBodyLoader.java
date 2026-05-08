package fr.maxlego08.menu.hooks.dialogs.button.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.dialogs.button.buttons.ZDialogPlainMessageBody;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class DialogPlainMessageBodyLoader extends ButtonLoader {

    public DialogPlainMessageBodyLoader(Plugin plugin) {
        super(plugin, "dialog_plain_message");
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        List<String> messages = configuration.getStringList(path+".messages");
        int width = configuration.getInt(path+".width",128);

        return new ZDialogPlainMessageBody(messages, width);
    }
}
