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
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;

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
    public Button load(@NonNull YamlDocument configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        CanvasManager canvasManager = this.menuPlugin.getCanvasManager();
        if (canvasManager == null) throw new DialogException("The dialog body " + path + " is a canvas, which needs dialog support");

        Section body = configuration.getSection(path);
        if (body == null || !body.contains("width") || !body.contains("height")) {
            throw new DialogException("The canvas " + path + " needs a width from 1 to " + ZCanvasBuilder.MAX_WIDTH + " and a height");
        }
        String where = "The canvas " + path;
        CanvasValue<Integer> width = CanvasValue.number(body, "width", 200, 1, ZCanvasBuilder.MAX_WIDTH, where);
        CanvasValue<Integer> height = CanvasValue.number(body, "height", 90, 1, 4096, where);

        List<CanvasElement> elements = new ArrayList<>();
        Section elementsSection = configuration.getSection(path + ".elements");
        if (elementsSection != null) {
            for (String name : elementsSection.getRoutesAsStrings(false)) {
                Section section = elementsSection.getSection(name);
                if (section == null) throw new DialogException("The canvas element " + name + " of " + path + " must be a section");
                String type = section.getString("type");
                if (type == null) throw new DialogException("The canvas element " + name + " of " + path + " needs a type");
                CanvasElementLoader loader = canvasManager.getElementLoader(type)
                        .orElseThrow(() -> new DialogException("The canvas element " + name + " of " + path + " has the unknown type " + type));
                elements.add(ConditionalElement.load(this.menuPlugin, loader, name, section, path));
            }
        }

        return new DialogCanvasBody(width, height, elements, YamlSections.getStringList(configuration, path + ".fallback"));
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, defaultButtonValue);
    }
}
