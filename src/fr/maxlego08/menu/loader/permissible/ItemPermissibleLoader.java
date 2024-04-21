package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.loader.ZPermissibleLoader;
import fr.maxlego08.menu.requirement.permissible.ZItemPermissible;
import org.bukkit.Material;

import java.io.File;
import java.util.List;

public class ItemPermissibleLoader extends ZPermissibleLoader {

    private final ButtonManager buttonManager;

    public ItemPermissibleLoader(ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
    }

    @Override
    public String getKey() {
        return "item";
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        Material material = Material.valueOf(accessor.getString("material").toUpperCase());
        int amount = accessor.getInt("amount");
        int modelId = accessor.getInt("modelId", 0);

        List<Action> denyActions = loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(buttonManager, accessor, "success", path, file);

        return new ZItemPermissible(material, amount, denyActions, successActions, modelId);
    }
}
