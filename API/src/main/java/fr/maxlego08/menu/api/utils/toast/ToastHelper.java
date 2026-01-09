package fr.maxlego08.menu.api.utils.toast;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ToastHelper {

    /**
     * Shows a toast to all the given players.
     *
     * @param players   the players to show the toast to
     * @param icon      the icon
     * @param message   the message
     * @param style     the style
     * @param modelData the model data
     * @param glowing   whether the icon is glowing
     */
    void showToast(Collection<? extends @NotNull Player> players, @NotNull String icon, @NotNull String message, @NotNull ToastType style, @Nullable Object modelData, boolean glowing);

    /**
     * Shows a toast to a varargs of players.
     *
     * @param icon      the icon
     * @param message   the message
     * @param style     the style
     * @param modelData the model data
     * @param glowing   whether the icon is glowing
     * @param players   the players to show the toast to
     */
    void showToast(@NotNull String icon,@NotNull String message,@NotNull ToastType style,@Nullable Object modelData, boolean glowing,@NotNull Player... players);

    /**
     * Shows a toast to all online players.
     *
     * @param icon      the icon
     * @param message   the message
     * @param style     the style
     * @param modelData the model data
     * @param glowing   whether the icon is glowing
     */
    void showToastToAll(@NotNull String icon,@NotNull String message,@NotNull ToastType style,@Nullable Object modelData, boolean glowing);
}
