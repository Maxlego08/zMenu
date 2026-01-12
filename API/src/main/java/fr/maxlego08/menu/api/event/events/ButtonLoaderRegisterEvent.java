package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.event.MenuEvent;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.pattern.PatternManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * <p>The event is called when the defaults buttons will be registered</p>
 */
@SuppressWarnings("unused")
public class ButtonLoaderRegisterEvent extends MenuEvent {

    private final ButtonManager buttonManager;
    private final InventoryManager inventoryManager;
    private final PatternManager patternManager;

    public ButtonLoaderRegisterEvent(@NotNull ButtonManager buttonManager, @NotNull InventoryManager inventoryManager,@NotNull PatternManager patternManager) {
        this.buttonManager = buttonManager;
        this.inventoryManager = inventoryManager;
        this.patternManager = patternManager;
    }

    @Contract(pure = true)
    @NotNull
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    @Contract(pure = true)
    @NotNull
    public PatternManager getPatternManager() {
        return patternManager;
    }

    /**
     * @return the buttonManager
     */
    @Contract(pure = true)
    @NotNull
    public ButtonManager getButtonManager() {
        return this.buttonManager;
    }

    /**
     * Register a ButtonLoader
     *
     * @param button the button to register
     */
    public void register(@NotNull ButtonLoader button) {
        this.buttonManager.register(button);
    }
}
