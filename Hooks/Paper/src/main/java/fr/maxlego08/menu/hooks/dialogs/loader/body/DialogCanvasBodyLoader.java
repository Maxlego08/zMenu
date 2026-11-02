package fr.maxlego08.menu.hooks.dialogs.loader.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.dialogs.body.DialogCanvasBody;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElementLoader;
import fr.maxlego08.menu.api.dialog.canvas.CanvasManager;
import fr.maxlego08.menu.api.dialog.canvas.CanvasValue;
import fr.maxlego08.menu.api.exceptions.DialogException;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.dialogs.canvas.ZCanvasBuilder;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.ConditionalElement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code dialog-canvas} bodies:
 *
 * <pre>
 * shop:
 *   type: dialog-canvas
 *   width: 320          # or a placeholder; 200 when it gives something else
 *   height: 180         # or a placeholder; 90 when it gives something else
 *   fallback:
 *     - "&lt;gold&gt;Shop"
 *   elements:
 *     title:
 *       type: text
 *       ...
 * </pre>
 * Every element also accepts {@code visible} and {@code view-requirement}, see {@link ConditionalElement}.
 */
@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogCanvasBodyLoader extends ButtonLoader {

    private final MenuPlugin menuPlugin;

    public DialogCanvasBodyLoader(MenuPlugin plugin) {
        super(plugin, "dialog-canvas", "canvas");
        this.menuPlugin = plugin;
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        CanvasManager canvasManager = this.menuPlugin.getCanvasManager();
        if (canvasManager == null) throw new DialogException("The dialog body " + path + " is a canvas, which needs dialog support");

        ConfigurationSection body = configuration.getConfigurationSection(path);
        if (body == null || !body.contains("width") || !body.contains("height")) {
            throw new DialogException("The canvas " + path + " needs a width from 1 to " + ZCanvasBuilder.MAX_WIDTH + " and a height");
        }
        String where = "The canvas " + path;
        CanvasValue<Integer> width = CanvasValue.number(body, "width", 200, 1, ZCanvasBuilder.MAX_WIDTH, where);
        CanvasValue<Integer> height = CanvasValue.number(body, "height", 90, 1, 4096, where);

        List<CanvasElement> elements = new ArrayList<>();
        ConfigurationSection elementsSection = configuration.getConfigurationSection(path + ".elements");
        if (elementsSection != null) {
            for (String name : elementsSection.getKeys(false)) {
                ConfigurationSection section = elementsSection.getConfigurationSection(name);
                if (section == null) throw new DialogException("The canvas element " + name + " of " + path + " must be a section");
                String type = section.getString("type");
                if (type == null) throw new DialogException("The canvas element " + name + " of " + path + " needs a type");
                CanvasElementLoader loader = canvasManager.getElementLoader(type)
                        .orElseThrow(() -> new DialogException("The canvas element " + name + " of " + path + " has the unknown type " + type));
                elements.add(ConditionalElement.load(this.menuPlugin, loader, name, section, path));
            }
        }

        return new DialogCanvasBody(width, height, elements, configuration.getStringList(path + ".fallback"));
    }
}
