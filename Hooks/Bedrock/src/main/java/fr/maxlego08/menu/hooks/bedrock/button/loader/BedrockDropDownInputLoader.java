package fr.maxlego08.menu.hooks.bedrock.button.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;
import fr.maxlego08.menu.hooks.bedrock.button.buttons.ZBedrockDropDownInput;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.BEDROCK_INVENTORY)
public class BedrockDropDownInputLoader extends ButtonLoader {

    public BedrockDropDownInputLoader(MenuPlugin plugin) {
        super(plugin, "bedrock_dropdown");
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String label = configuration.getString(path + ".text", "");
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

        return new ZBedrockDropDownInput(label, singleOptionList);
    }
}
