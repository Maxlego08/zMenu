package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.loader.ZPermissibleLoader;
import fr.maxlego08.menu.requirement.permissible.ZVaultPermissible;

import java.io.File;
import java.util.List;

public class VaultPermissibleLoader extends ZPermissibleLoader {

    private final ButtonManager buttonManager;

    public VaultPermissibleLoader(ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
    }

    @Override
    public String getKey() {
        return "money";
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        List<Action> denyActions = loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(buttonManager, accessor, "success", path, file);
        return new ZVaultPermissible(denyActions, successActions, accessor.getDouble("amount"));
    }
}
