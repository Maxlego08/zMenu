package fr.maxlego08.menu.api.engine;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface InventoryEngine extends BaseInventory {

    @Contract(pure = true)
    @NotNull
    List<Inventory> getOldInventories();

    @Contract(pure = true)
    @NotNull
    List<Button> getButtons();

    void buildButton(@Nullable Button button);

    void displayButton(@NotNull Button button);

    void displayFinalButton(@NotNull Button button, int... slots);

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
