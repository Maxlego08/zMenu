package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.permissible.ZItemPermissible;
import org.bukkit.Material;

import java.io.File;

public class ItemPermissibleLoader implements PermissibleLoader {

    @Override
    public String getKey() {
        return "item";
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        Material material = Material.valueOf(accessor.getString("material").toUpperCase());
        int amount = accessor.getInt("amount");
        return new ZItemPermissible(material, amount);
    }
}
