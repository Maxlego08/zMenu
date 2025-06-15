package fr.maxlego08.menu.pattern;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;

import java.util.List;

public class ZPattern implements Pattern {

    private final String name;
    private final List<Button> buttons;
    private final int inventorySize;
    private final boolean enableMultiPage;

    public ZPattern(String name, List<Button> buttons, int inventorySize, boolean enableMultiPage) {
        this.name = name;
        this.buttons = buttons;
        this.inventorySize = inventorySize;
        this.enableMultiPage = enableMultiPage;
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

    @Override
    public boolean enableMultiPage() {
        return this.enableMultiPage;
    }
}
