package fr.maxlego08.menu.inventory.zinv;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.inventory.setter.AnvilInventorySetter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ZAnvilInventory extends ZInventory implements AnvilInventorySetter {
    private final List<Requirement> renameRequirements;

    public ZAnvilInventory(Plugin plugin, String name, String fileName, int size, List<Button> buttons, List<Requirement> renameRequirements) {
        super(plugin, name, fileName, size, buttons);
        this.renameRequirements = renameRequirements;
    }

    @Override
    public @NotNull List<Requirement> getRenameRequirements() {
        return renameRequirements;
    }
}
