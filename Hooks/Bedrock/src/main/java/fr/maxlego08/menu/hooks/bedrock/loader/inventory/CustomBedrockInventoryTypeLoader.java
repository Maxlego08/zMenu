package fr.maxlego08.menu.hooks.bedrock.loader.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.inputs.BedrockInputButton;
import fr.maxlego08.menu.hooks.bedrock.inventory.ZCustomBedrockInventory;
import fr.maxlego08.menu.hooks.bedrock.loader.AbstractBedrockInventoryTypeLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class CustomBedrockInventoryTypeLoader extends AbstractBedrockInventoryTypeLoader<ZCustomBedrockInventory> {

    @Override
    protected ZCustomBedrockInventory loadInventory(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String title) {
        List<BedrockInputButton> inputButtons = this.loadButtons(configuration, file, "buttons", BedrockInputButton.class, BedrockInputButton::setKey, menuPlugin);
        return new ZCustomBedrockInventory(menuPlugin, file.getName(), title, inputButtons);
    }
}
