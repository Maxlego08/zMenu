package fr.maxlego08.menu.hooks.dialogs.loader.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.dialogs.body.DialogPlainMessageBody;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<String, List<String>> localizedMessages = null;

        ConfigurationSection localizedMessageSection = configuration.getConfigurationSection(path + ".localized-messages");
        if (localizedMessageSection != null) {
            localizedMessages = new HashMap<>();
            for (String locale : localizedMessageSection.getKeys(false)) {
                List<String> localizedMessageList = configuration.getStringList(path + ".localized-messages." + locale);
                localizedMessages.put(locale, localizedMessageList);
            }
        }

        return new DialogPlainMessageBody(messages, width, localizedMessages);
    }
}
