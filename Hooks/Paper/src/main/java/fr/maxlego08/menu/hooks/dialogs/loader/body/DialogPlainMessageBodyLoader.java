package fr.maxlego08.menu.hooks.dialogs.loader.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.dialogs.body.DialogPlainMessageBody;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogPlainMessageBodyLoader extends ButtonLoader {

    public DialogPlainMessageBodyLoader(MenuPlugin plugin) {
        super(plugin, "dialog_plain_message", "plain_message");
    }

    @Override
    public Button load(@NonNull YamlDocument configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        List<String> messages = YamlSections.getStringList(configuration, path+".messages");
        int width = configuration.getInt(path+".width",128);

        Map<String, List<String>> localizedMessages = null;

        Section localizedMessageSection = configuration.getSection(path + ".localized-messages");
        if (localizedMessageSection != null) {
            localizedMessages = new HashMap<>();
            for (String locale : localizedMessageSection.getRoutesAsStrings(false)) {
                List<String> localizedMessageList = YamlSections.getStringList(configuration, path + ".localized-messages." + locale);
                localizedMessages.put(locale, localizedMessageList);
            }
        }

        return new DialogPlainMessageBody(messages, width, localizedMessages);
    }

    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, defaultButtonValue);
    }
}
