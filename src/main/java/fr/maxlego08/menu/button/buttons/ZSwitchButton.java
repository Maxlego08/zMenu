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

        String result = inventoryEngine.getPlugin().parse(player, this.placeholder);

        for (SwitchCaseButton button : this.buttons) {

            String value = button.value();

            if (value.startsWith(">=")) {

                String newValue = value.substring(2);
                if (Integer.parseInt(result) >= Integer.parseInt(newValue)) return button.button();

            } else if (value.startsWith("<=")) {

                String newValue = value.substring(2);
                if (Integer.parseInt(result) <= Integer.parseInt(newValue)) return button.button();

            } else if (value.startsWith(">")) {

                String newValue = value.substring(1);
                if (Integer.parseInt(result) > Integer.parseInt(newValue)) return button.button();

            } else if (value.startsWith("<")) {

                String newValue = value.substring(1);
                if (Integer.parseInt(result) < Integer.parseInt(newValue)) return button.button();

            } else if (value.equals(result)) return button.button();
        }

        return super.getDisplayButton(inventoryEngine, player);
    }
}
