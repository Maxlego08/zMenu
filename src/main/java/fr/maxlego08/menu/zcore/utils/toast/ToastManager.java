package fr.maxlego08.menu.zcore.utils.toast;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.toast.ToastHelper;
import fr.maxlego08.menu.api.utils.toast.ToastType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;

public class ToastManager implements ToastHelper {

    private final MenuPlugin plugin;
    private final AdvancementHandler advancementHandler;

    public ToastManager(MenuPlugin plugin) {
        this.plugin = plugin;
        this.advancementHandler = new AdvancementHandler(plugin);
    }

    public ToastBuilder createToast() {
        return new ToastBuilder(this.advancementHandler);
    }

    @Override
    public void showToast(Collection<? extends Player> players, String icon, String message, ToastType style, Object modelData, boolean glowing) {
        createToast().withIcon(icon).withMessage(message).withStyle(style).withModelData(modelData).setGlowing(glowing).to(players).show();
    }

    @Override
    public void showToastToAll(String icon, String message, ToastType style, Object modelData, boolean glowing) {
        createToast().withIcon(icon).withMessage(message).withStyle(style).withModelData(modelData).setGlowing(glowing).toAll().show();
    }

    @Override
    public void showToast(String icon, String message, ToastType style, Object modelData, boolean glowing, Player... players) {
        showToast(Arrays.asList(players), icon, message, style, modelData, glowing);
    }
}