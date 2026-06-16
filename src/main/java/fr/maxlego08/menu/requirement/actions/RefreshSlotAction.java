package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RefreshSlotAction extends ActionHelper {

    private final List<Integer> slots;
    private final boolean isInPlayerInventory;

    public RefreshSlotAction(List<Integer> slots, boolean inPlayerInventory) {
        this.slots = slots;
        this.isInPlayerInventory = inPlayerInventory;
    }

    @Override
    protected void execute(
            @NotNull Player player,
            @Nullable Button button,
            @NotNull InventoryEngine inventoryEngine,
            @NotNull Placeholders placeholders
    ) {

        List<Button> buttons = inventoryEngine.getButtons();

        for (Button b : buttons) {

            if (b == null) continue;
            if (b.isPlayerInventory() != isInPlayerInventory) continue;

            int[] matchingSlots = b.getSlots().stream()
                    .filter(slots::contains)
                    .mapToInt(Integer::intValue)
                    .toArray();

            if (matchingSlots.length == 0) continue;

            inventoryEngine.displayFinalButton(b, placeholders, matchingSlots);
        }
    }
}