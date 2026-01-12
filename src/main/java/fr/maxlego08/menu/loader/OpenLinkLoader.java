package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class OpenLinkLoader implements Loader<OpenLink> {

    private final MenuPlugin plugin;

    public OpenLinkLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public OpenLink load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects) throws InventoryException {

        Action action = Action.valueOf(configuration.getString(path + "action", "OPEN_URL").toUpperCase());
        String link = configuration.getString(path + "link");
        String message = configuration.getString(path + "message");
        String replace = configuration.getString(path + "replace");
        List<String> hover = configuration.getStringList(path + "hover");

        return new ZOpenLink(this.plugin, action, message, link, replace, hover);
    }

    @Override
    public void save(OpenLink object, @NonNull YamlConfiguration configuration, @NonNull String path, File file, Object... objects) {

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
