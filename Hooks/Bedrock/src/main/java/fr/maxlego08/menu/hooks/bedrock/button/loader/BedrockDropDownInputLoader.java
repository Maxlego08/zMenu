package fr.maxlego08.menu.hooks.bedrock.button.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.bedrock.inputs.BedrockDropDownInput;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.utils.record.bedrock.BedrockSimpleOption;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.BEDROCK_INVENTORY)
public class BedrockDropDownInputLoader extends ButtonLoader {

    public BedrockDropDownInputLoader(MenuPlugin plugin) {
        super(plugin, "bedrock_dropdown");
    }

    @Override
    public Button load(@NonNull YamlDocument configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String label = configuration.getString(path + ".text", "");
        List<BedrockSimpleOption> singleOptionList = new ArrayList<>();

        if (configuration.isSection(path + ".options")) {
            boolean initialAlreadySet = false;
            for (String optionKey : configuration.getSection(path + ".options").getRoutesAsStrings(false)) {
                String optionPath = path + ".options." + optionKey;

                configuration.getString(optionPath + ".id", optionKey);
                String display = configuration.getString(optionPath + ".display", "");
                boolean initialValue = configuration.getBoolean(optionPath + ".initial", false);

                if (initialAlreadySet) {
                    initialValue = false;
                } else if (initialValue) {
                    initialAlreadySet = true;
                }

                singleOptionList.add(new BedrockSimpleOption(display, initialValue));
            }
        }

        return new BedrockDropDownInput(label, singleOptionList);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, defaultButtonValue);
    }
}
