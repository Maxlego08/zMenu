package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.event.MenuEvent;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.pattern.PatternManager;

/**
 * <p>The event is called when the defaults buttons will be registered</p>
 */
public class ButtonLoaderRegisterEvent extends MenuEvent {

    private final ButtonManager buttonManager;
    private final InventoryManager inventoryManager;
    private final PatternManager patternManager;

    public ButtonLoaderRegisterEvent(ButtonManager buttonManager, InventoryManager inventoryManager, PatternManager patternManager) {
        this.buttonManager = buttonManager;
        this.inventoryManager = inventoryManager;
        this.patternManager = patternManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public PatternManager getPatternManager() {
        return patternManager;
    }

    /**
     * @return the buttonManager
     */
    public ButtonManager getButtonManager() {
        return this.buttonManager;
    }

    /**
     * Register a ButtonLoader
     *
     * @param button
     */
    public void register(ButtonLoader button) {
        this.buttonManager.register(button);
    }

}
