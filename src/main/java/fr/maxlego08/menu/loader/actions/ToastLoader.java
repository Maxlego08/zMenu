package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.api.utils.toast.ToastType;
import fr.maxlego08.menu.requirement.actions.ToastAction;
import org.jspecify.annotations.NonNull;

import java.io.File;

public class ToastLoader extends ActionLoader {

    private final MenuPlugin plugin;

    public ToastLoader(MenuPlugin plugin) {
        super("toast", "send toast");
        this.plugin = plugin;
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {

        String message = accessor.getString("message", "Default message");
        String material = accessor.getString("material", "PAPER");
        String modelId = accessor.getString("model-id", "0");
        ToastType toastType = ToastType.valueOf(accessor.getString("toast-type", ToastType.CHALLENGE.toString()).toUpperCase());
        boolean glowing = accessor.getBoolean("glowing", false);

        return new ToastAction(this.plugin, material, message, toastType, modelId, glowing);
    }
}
