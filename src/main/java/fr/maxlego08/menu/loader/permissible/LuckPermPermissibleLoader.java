package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.annotations.AutoPermissibleLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.luckperms.ZLuckPermPermissible;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

@AutoPermissibleLoader
@RequiresPlugin("LuckPerms")
public class LuckPermPermissibleLoader extends PermissibleLoader {

    private final ButtonManager buttonManager;

    public LuckPermPermissibleLoader(ButtonManager buttonManager) {
        super("luckperm");
        this.buttonManager = buttonManager;
    }

    @Override
    public Permissible load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        List<Action> denyActions = this.loadAction(this.buttonManager, accessor, "deny", path, file);
        List<Action> successActions = this.loadAction(this.buttonManager, accessor, "success", path, file);
        return new ZLuckPermPermissible(denyActions, successActions, accessor.getString("group"));
    }
}
