package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Locale;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;

public class OpenLinkLoader implements Loader<OpenLink> {

    private final MenuPlugin plugin;

    public OpenLinkLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public OpenLink load(@NonNull YamlDocument configuration, @NonNull String path, Object... objects) throws InventoryException {

        Action action = Action.valueOf(configuration.getString(path + "action", "OPEN_URL").toUpperCase(Locale.ROOT));
        String link = configuration.getString(path + "link");
        String message = configuration.getString(path + "message");
        String replace = configuration.getString(path + "replace");
        List<String> hover = YamlSections.getStringList(configuration, path + "hover");

        return new ZOpenLink(this.plugin, action, message, link, replace, hover);
    }

    @Override
    @Deprecated
    public OpenLink load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException  {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, objects);
    }

}
