package fr.maxlego08.menu.api.engine;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.PlayerTitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface BaseInventory extends InventoryHolder {

    @Contract(pure = true)
    @Nullable
    MenuPlugin getPlugin();

    @Contract(pure = true)
    @Nullable
    Player getPlayer();

    @Contract(pure = true)
    boolean isClose();

    @Contract("_, null -> null")
    @Nullable
    ItemButton addItem(int slot,@Nullable ItemStack itemStack);

    @Contract("_, null,_ -> null")
    @Nullable
    ItemButton addItem(int slot,@Nullable ItemStack itemStack, boolean enableAntiDupe);

    @Contract("_, _, null -> null")
    @Nullable
    ItemButton addItem(boolean inPlayerInventory, int slot,@Nullable ItemStack itemStack);

    @Contract("_, _, null, _ -> null")
    @Nullable
    ItemButton addItem(boolean inPlayerInventory, int slot,@Nullable ItemStack itemStack, boolean enableAntiDupe);

    @Contract(pure = true)
    @Nullable
    String getGuiName();

    @Contract(pure = true)
    @NotNull
    Inventory getSpigotInventory();

    @Contract(pure = true)
    @NotNull Inventory getInventory();

    @Contract(pure = true)
    @Nullable
    Object[] getArgs();

    @Contract(pure = true)
    int getPage();

    void removeItem(int slot);

    void removePlayerItem(int slot);

    void clearItem();

    @Contract(pure = true)
    @NotNull
    Map<Integer, ItemButton> getItems();

    @Contract(pure = true)
    @NotNull
    Map<Integer, ItemButton> getPlayerInventoryItems();

    @Contract(pure = true)
    boolean isDisableClick();

    void setDisableClick(boolean disableClick);

    @Contract(pure = true)
    boolean isDisablePlayerInventoryClick();

    void setDisablePlayerInventoryClick(boolean disablePlayerInventoryClick);

    void setPlayerTitleAnimation(PlayerTitleAnimation playerTitleAnimation);

    @Nullable
    PlayerTitleAnimation getPlayerTitleAnimation();

    void setTitleAnimation(TitleAnimation animation);

    TitleAnimation getTitleAnimation();

}
