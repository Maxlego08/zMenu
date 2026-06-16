package fr.maxlego08.menu.inventory.setter;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.inventory.ContainerInventory;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.ClearInvType;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import org.bukkit.event.inventory.InventoryType;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ContainerInventorySetter extends ContainerInventory {
    void setType(InventoryType type);

    void setCancelItemPickup(boolean ItemPickupDisabled);

    void setFillItemStack(MenuItemStack fillItemStack);

    void setUpdateInterval(int updateInterval);

    void setFile(File file);

    void setOpenRequirement(Requirement openRequirement);

    void setOpenWithItem(OpenWithItem openWithItem);

    void setTranslatedNames(Map<String, String> translatedNames);

    void setClearInventory(boolean clearInventory);

    void setPatterns(List<Pattern> patterns);

    void setTargetPlayerNamePlaceholder(String targetPlaceholder);

    void setOpenActions(List<Action> openActions);

    void setCloseActions(List<Action> closeActions);

    void setClickLimiterEnabled(boolean enabled);

    void setClearInvType(ClearInvType clearInvType);
}
