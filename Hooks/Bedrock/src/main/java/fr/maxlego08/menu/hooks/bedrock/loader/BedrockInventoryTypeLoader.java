package fr.maxlego08.menu.hooks.bedrock.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.exceptions.InventoryButtonException;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.hooks.bedrock.AbstractBedrockInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public interface BedrockInventoryTypeLoader<T extends AbstractBedrockInventory<?,?,?>> {

    @Nullable
    T load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String title);

    static <U extends Button> List<U> loadButtons(
            YamlConfiguration configuration,
            File file,
            String sectionKey,
            Class<U> buttonClass,
            BiConsumer<U, String> postProcess,
            MenuPlugin menuPlugin
    ) {
        List<U> buttons = new ArrayList<>();

        ConfigurationSection section = configuration.getConfigurationSection(sectionKey);

        if (section == null) {
            return buttons;
        }

        Loader<Button> loader =menuPlugin.getButtonManager()
                .getLoaderButton(menuPlugin, file, 54, new HashMap<>());

        for (String key : section.getKeys(false)) {
            String path = sectionKey + "." + key + ".";
            try {
                Button button = loader.load(configuration, path, key);
                U typedButton = getButtonType(button, buttonClass, path, file);

                if (postProcess != null) {
                    Button current = button.getMasterParentButton();
                    while (current != null) {
                        if (buttonClass.isInstance(current)) {
                            postProcess.accept(buttonClass.cast(current), key);
                        }
                        current = current.getElseButton();
                    }
                }

                buttons.add(typedButton);
            } catch (Exception exception) {
                Logger.info(exception.getMessage(), Logger.LogType.ERROR);
            }
        }
        return buttons;
    }



    static  <T extends Button> T getButtonType(Button button, Class<T> verifClass, String path, File file) throws InventoryButtonException {
        if (verifClass.isInstance(button)) {
            if (button.getElseButton() != null){
                return getButtonType(button.getElseButton(), verifClass, path, file);
            }
            return (T) button;
        } else {
            throw new InventoryButtonException("The type " + button.getName() + " for the button " + path + " is not valid for this section" + file.getAbsolutePath());
        }
    }
}
