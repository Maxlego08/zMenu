package fr.maxlego08.menu.api.inventory;

import fr.maxlego08.menu.api.requirement.Requirement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AnvilInventory extends ContainerInventory {

    @NotNull
    List<Requirement> getRenameRequirements();

}
