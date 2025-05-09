package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.ShopkeeperAction;

import java.io.File;

public class ShopkeeperLoader implements ActionLoader {
    @Override
    public String getKey() {
        return "shopkeeper";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String name = accessor.getString("name", "error_name");
        return new ShopkeeperAction(name);
    }
}
