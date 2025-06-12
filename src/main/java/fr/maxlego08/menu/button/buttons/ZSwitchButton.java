package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.SwitchButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.SwitchCaseButton;
import org.bukkit.entity.Player;

import java.util.List;

public class ZSwitchButton extends SwitchButton {

    private final String placeholder;
    private final List<SwitchCaseButton> buttons;

    public ZSwitchButton(String placeholder, List<SwitchCaseButton> buttons) {
        this.placeholder = placeholder;
        this.buttons = buttons;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public List<SwitchCaseButton> getButtons() {
        return buttons;
    }

    @Override
    public Button getDisplayButton(InventoryEngine inventoryEngine, Player player) {

        if (buttons.isEmpty()) return super.getDisplayButton(inventoryEngine, player);

        if (buttons.size() == 1) return buttons.getFirst().button();

        // ToDo

        System.out.println("Oui c'est en to do");

        return super.getDisplayButton(inventoryEngine, player);
    }
}
