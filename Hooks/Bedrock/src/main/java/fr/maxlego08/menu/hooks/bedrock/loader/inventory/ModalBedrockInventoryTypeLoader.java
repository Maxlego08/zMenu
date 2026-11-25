package fr.maxlego08.menu.hooks.bedrock.loader.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.components.BasicBedrockComponentButton;
import fr.maxlego08.menu.hooks.bedrock.inventory.ZModalBedrockInventory;
import fr.maxlego08.menu.hooks.bedrock.loader.AbstractBedrockInventoryTypeLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockInventoryTypeLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class ModalBedrockInventoryTypeLoader extends AbstractBedrockInventoryTypeLoader<ZModalBedrockInventory> {

    @Override
    protected ZModalBedrockInventory loadInventory(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String title) {
        String content = configuration.getString("content", "");
        List<BasicBedrockComponentButton> bodyButtons = BedrockInventoryTypeLoader.loadButtons(configuration, file, "buttons", BasicBedrockComponentButton.class, null, menuPlugin);
        int buttonSize = bodyButtons.size();
        if (buttonSize < 2) {
            Logger.error("MODAL type requires at least 2 button. Found: " + buttonSize + ". Please add more buttons to the configuration.");
            return null;
        } else if (buttonSize > 2) {
            Logger.info("MODAL type supports a maximum of 2 buttons. Only the first two are considered.", Logger.LogType.ERROR);
            bodyButtons = bodyButtons.subList(0, 2);
        }
        return new ZModalBedrockInventory(menuPlugin, file.getName(), title, content, bodyButtons);
    }
}
