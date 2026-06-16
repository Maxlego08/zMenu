package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;
import fr.maxlego08.menu.hooks.dialogs.button.buttons.ZDialogSingleOptionInput;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogSingleOptionInputLoader extends ButtonLoader {

    public DialogSingleOptionInputLoader(MenuPlugin plugin) {
        super(plugin, "dialog_single_option");
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {

        int width = configuration.getInt(path + ".width", 200);
        String label = configuration.getString(path + ".label", "");
        boolean labelVisible = configuration.getBoolean(path + ".label-visible", true);
        List<SingleOption> singleOptionList = new ArrayList<>();

        if (configuration.isConfigurationSection(path + ".options")) {
            boolean initialAlreadySet = false;
            for (String optionKey : configuration.getConfigurationSection(path + ".options").getKeys(false)) {
                String optionPath = path + ".options." + optionKey;

                String id = configuration.getString(optionPath + ".id", optionKey);
                String display = configuration.getString(optionPath + ".display", "");
                boolean initialValue = configuration.getBoolean(optionPath + ".initial", false);

                if (initialAlreadySet) {
                    initialValue = false;
                } else if (initialValue) {
                    initialAlreadySet = true;
                }

                SingleOption singleOption = new SingleOption(id, display, initialValue);
                singleOptionList.add(singleOption);
            }
        }
        ZDialogSingleOptionInput zDialogSingleOptionInput = new ZDialogSingleOptionInput(label, labelVisible, singleOptionList);
        zDialogSingleOptionInput.setWidth(width);
        return zDialogSingleOptionInput;
    }
}
