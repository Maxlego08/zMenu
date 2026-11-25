package fr.maxlego08.menu.hooks.bedrock.loader.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.inputs.BedrockInputButton;
import fr.maxlego08.menu.hooks.bedrock.inventory.ZCustomBedrockInventory;
import fr.maxlego08.menu.hooks.bedrock.loader.AbstractBedrockInventoryTypeLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockInventoryTypeLoader;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class CustomBedrockInventoryTypeLoader extends AbstractBedrockInventoryTypeLoader<ZCustomBedrockInventory> {

    @Override
    protected ZCustomBedrockInventory loadInventory(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String title) {
        List<BedrockInputButton> inputButtons = BedrockInventoryTypeLoader.loadButtons(configuration, file, "buttons", BedrockInputButton.class, BedrockInputButton::setKey, menuPlugin);
        return new ZCustomBedrockInventory(menuPlugin, file.getName(), title, inputButtons);
    }
}
