package fr.maxlego08.menu.pattern;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;

import java.util.List;

public class ZPattern implements Pattern {

    private final String name;
    private final List<Button> buttons;
    private final int inventorySize;

    public ZPattern(String name, List<Button> buttons, int inventorySize) {
        this.name = name;
        this.buttons = buttons;
        this.inventorySize = inventorySize;
    }

    @Override
    public int getInventorySize() {
        return inventorySize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Button> getButtons() {
        return buttons;
    }
}
