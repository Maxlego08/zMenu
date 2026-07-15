package fr.maxlego08.menu.hooks.dialogs.loader.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.dialogs.body.DialogPlainMessageBody;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.List;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogPlainMessageBodyLoader extends ButtonLoader {

    public DialogPlainMessageBodyLoader(MenuPlugin plugin) {
        super(plugin, "dialog_plain_message", "plain_message");
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        List<String> messages = configuration.getStringList(path+".messages");
        int width = configuration.getInt(path+".width",128);

        return new DialogPlainMessageBody(messages, width);
    }
}
