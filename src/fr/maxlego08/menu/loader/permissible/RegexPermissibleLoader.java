package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.permissible.ZRegexPermissible;

import java.io.File;

public class RegexPermissibleLoader implements PermissibleLoader {

    @Override
    public String getKey() {
        return "regex";
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        String placeholder = accessor.getString("placeholder", accessor.getString("placeHolder"));
        String regex = accessor.getString("regex");
        return new ZRegexPermissible(regex, placeholder);
    }
}
