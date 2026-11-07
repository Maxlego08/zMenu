package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.event.CancelledMenuEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player clicks a {@code custom-click} button of a zMenu dialog,
 * before the actions of the button are executed.
 * <p>
 * Cancelling this event prevents the actions of the button from being executed.
 */
public class PlayerDialogClickEvent extends CancelledMenuEvent {

    private final Player player;
    private final String dialogName;
    private final String buttonName;

    public PlayerDialogClickEvent(@NotNull Player player, @NotNull String dialogName, @NotNull String buttonName) {
        this.player = player;
        this.dialogName = dialogName;
        this.buttonName = buttonName;
    }

    @Contract(pure = true)
    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return the file name of the dialog, without the {@code .yml} extension
     */
    @Contract(pure = true)
    @NotNull
    public String getDialogName() {
        return this.dialogName;
    }

    /**
     * @return the configuration key of the clicked button, for example {@code button1} for
     * {@code multi-actions.button1}, {@code yes} / {@code no} for a confirmation dialog,
     * {@code notice} or {@code exit-button}
     */
    @Contract(pure = true)
    @NotNull
    public String getButtonName() {
        return this.buttonName;
    }
}
