package fr.maxlego08.menu.hooks.bedrock.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.hooks.bedrock.AbstractBedrockInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public abstract class AbstractBedrockInventoryTypeLoader<T extends AbstractBedrockInventory<?, ?, ?>> implements BedrockInventoryTypeLoader<T> {

    @Override
    public @Nullable T load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String title) {
        T inventory = this.loadInventory(menuPlugin, file, configuration, title);
        if (inventory == null) {
            return null;
        }
        inventory.setFile(file);

        try {
            List<Requirement> actions = menuPlugin.getButtonManager().loadRequirements(configuration, "actions", file);
            inventory.setRequirements(actions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return inventory;
    }

    protected abstract T loadInventory(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String title);
}
