package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class OpenLinkLoader implements Loader<OpenLink> {

    @Override
    public OpenLink load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException {

        Action action = Action.valueOf(configuration.getString(path + "action", "OPEN_URL").toUpperCase());
        String link = configuration.getString(path + "link");
        String message = configuration.getString(path + "message");
        String replace = configuration.getString(path + "replace");
        List<String> hover = configuration.getStringList(path + "hover");

        return new ZOpenLink(action, message, link, replace, hover);
    }

    @Override
    public void save(OpenLink object, YamlConfiguration configuration, String path, File file, Object... objects) {

        configuration.set(path + "action", "OPEN_URL");
        configuration.set(path + "link", object.getLink());
        configuration.set(path + "message", object.getMessage());
        configuration.set(path + "replace", object.getReplace());
        configuration.set(path + "hover", object.getHover());

        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
