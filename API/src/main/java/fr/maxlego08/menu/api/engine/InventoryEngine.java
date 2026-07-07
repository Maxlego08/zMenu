package fr.maxlego08.menu.api.engine;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.utils.PerformanceDebug;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface InventoryEngine extends BaseInventory {

    default @NotNull PerformanceDebug getPerformanceDebug() {
        return PerformanceDebug.disabled();
    }

    @Contract(pure = true)
    @NotNull
    List<Inventory> getOldInventories();

    @Contract(pure = true)
    @NotNull
    List<Button> getButtons();

    /**
     * @deprecated use buildButton with Placeholders instead
     * @param button the button to build
     */
    @Deprecated
    void buildButton(@Nullable Button button);

    void buildButton(@Nullable Button button, @NotNull Placeholders placeholders);

    /**
     * @deprecated use displayButton with Placeholders instead
     * @param button the button to display
     */
    @Deprecated
    void displayButton(@NotNull Button button);

    void displayButton(@NotNull Button button, @NotNull Placeholders placeholders);

    /**
     * @deprecated use displayFinalButton with Placeholders instead
     * @param button the button to display
     * @param slots the slots to display the button in
     */
    @Deprecated
    void displayFinalButton(@NotNull Button button, int... slots);

    void displayFinalButton(@NotNull Button button, @NotNull Placeholders placeholders, int... slots);

    @Contract(pure = true)
    @Nullable
    Inventory getMenuInventory();

    @Contract(pure = true)
    int getMaxPage();

    @Contract(pure = true)
    void setMaxPage(int maxPage);

    @Contract(pure = true)
    void cancel(int slot);
}
