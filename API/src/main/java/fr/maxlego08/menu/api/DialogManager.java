package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.configuration.ConfigManagerInt;
import fr.maxlego08.menu.api.exceptions.DialogException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public interface DialogManager {

    DialogInventory loadDialog(Plugin plugin, String fileName) throws DialogException;

    Optional<DialogInventory> getDialog(String name);

    Optional<DialogInventory> getDialog(String pluginName, String fileName);

    Optional<DialogInventory> getDialog(Plugin plugin, String fileName);

    void deleteDialog(String name);

    void deleteDialog(Plugin plugin);

    void loadDialogs();

    DialogInventory loadInventory(Plugin plugin, String fileName) throws DialogException, InventoryException;

    DialogInventory loadInventory(Plugin plugin, File file) throws DialogException, InventoryException;

    DialogInventory loadInventory(Plugin plugin, String fileName, Class<? extends DialogInventory> dialogClass) throws DialogException, InventoryException;

    DialogInventory loadInventory(Plugin plugin, File file, Class<? extends DialogInventory> dialogClass) throws DialogException, InventoryException;

    //void registerBuilder(DialogBuilder builder);

    void openDialog(Player player, DialogInventory dialog);

    //Optional<DialogBuilder> getDialogBuilder(DialogBodyType type);

    void openDialog(Player player, DialogInventory dialogInventory, List<Inventory> oldInventories);

    Collection<DialogInventory> getDialogs();

    void reloadDialogs();

    ConfigManagerInt getConfigManager();

    Set<String> getDialogNames();

    /**
     * Creates the custom click action of a dialog button. It is kept in the player's click table, like the
     * click areas of canvases, instead of a Paper click callback: opening another dialog for the player
     * forgets it, so the buttons of the dialogs opened before stop working.
     *
     * @param viewer   The player the dialog is shown to.
     * @param action   What a click does, on the player's thread, with the values of the dialog inputs.
     * @param uses     How many clicks work, or {@code ClickCallback.UNLIMITED_USES}.
     * @param lifetime How long the click works, or null for {@code ClickCallback.DEFAULT_LIFETIME}.
     * @return The dialog action.
     */
    @NotNull
    DialogAction clickAction(@NotNull Player viewer, @NotNull BiConsumer<Player, DialogResponseView> action, int uses, @Nullable TemporalAmount lifetime);
}
