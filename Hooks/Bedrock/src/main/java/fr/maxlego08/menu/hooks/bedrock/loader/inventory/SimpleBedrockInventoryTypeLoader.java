package fr.maxlego08.menu.hooks.bedrock.loader.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.components.BedrockComponentButton;
import fr.maxlego08.menu.hooks.bedrock.inventory.ZSimpleBedrockInventory;
import fr.maxlego08.menu.hooks.bedrock.loader.AbstractBedrockInventoryTypeLoader;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockInventoryTypeLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class SimpleBedrockInventoryTypeLoader extends AbstractBedrockInventoryTypeLoader<ZSimpleBedrockInventory> {

    @Override
    protected ZSimpleBedrockInventory loadInventory(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String title) {
        String content = configuration.getString("content", "");
        List<BedrockComponentButton> bodyButtons = BedrockInventoryTypeLoader.loadButtons(configuration, file, "buttons", BedrockComponentButton.class, null, menuPlugin);
        return new ZSimpleBedrockInventory(menuPlugin, file.getName(), title, content, bodyButtons);
    }
}
