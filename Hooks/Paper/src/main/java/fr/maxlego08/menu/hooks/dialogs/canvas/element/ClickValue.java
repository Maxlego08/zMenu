package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.loader.DialogActionLoader;
import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogInventoryTypeLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.dialogaction.ZCustomClickDialogActionLoader;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.ClickEvent;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Set;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

/**
 * The {@code click} of a button or an item: a dialog action, loaded like the actions of dialog buttons, used as
 * the click event of the canvas area instead of zMenu actions.
 *
 * <pre>
 * click:
 *   type: static
 *   static-type: OPEN_URL          # OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, COPY_TO_CLIPBOARD
 *   url: "https://discord.gg/..."  # placeholders are resolved when the canvas is drawn
 * </pre>
 * The {@code custom-click} type is refused: {@code actions}, {@code click-requirement} and {@code refresh}
 * already run zMenu actions on a click, so an element takes one or the other.
 *
 * @param action The dialog action.
 * @param config The section it was read from, written back by {@link #serialize}.
 */
public record ClickValue(@NotNull ZDialogAction action, @NotNull Section config) {

    private static final Set<String> ACTION_KEYS = Set.of("actions", "click-requirement", "refresh");

    /**
     * @return The {@code click} of an element, or null when it has none.
     */
    public static @Nullable ClickValue read(@NotNull MenuPlugin plugin, @NotNull Section section, @NotNull String name, @NotNull String source) {
        Section click = section.getSection("click");
        if (click == null) {
            if (section.contains("click")) throw ElementSections.error(name, source, "needs a section in click, with a type such as static");
            return null;
        }
        for (String key : ACTION_KEYS) {
            if (section.contains(key)) throw ElementSections.error(name, source, "cannot have both click and " + key + ": click replaces the zMenu actions");
        }
        String type = click.getString("type", "static");
        DialogActionLoader dialogActionLoader = DialogInventoryTypeLoader.findActionLoader(plugin, type).orElseThrow(() -> ElementSections.error(name, source, "has an unknown click type: " + type));
        if (dialogActionLoader instanceof ZCustomClickDialogActionLoader) {
            throw ElementSections.error(name, source, "cannot use a custom-click in click: write the zMenu actions in actions instead");
        }

        if (!(section.getRoot() instanceof YamlDocument configuration) || YamlSections.getCurrentPath(click) == null) {
            throw ElementSections.error(name, source, "must be read from a YAML file");
        }
        try {
            return new ClickValue(dialogActionLoader.load(plugin, configuration, YamlSections.getCurrentPath(click), new File(source)), click);
        } catch (IllegalArgumentException exception) {
            throw ElementSections.error(name, source, "has an invalid click: " + exception.getMessage());
        }
    }

    /**
     * @return The click event of the area, or null when the action cannot be used in a canvas.
     */
    public @Nullable ClickEvent clickEvent(@NotNull CanvasRenderContext context) {
        DialogAction built = this.action.build(List.of(), context.player(), context.plugin(), context.engine(), null, context.placeholders());
        return switch (built) {
            case DialogAction.StaticAction value -> value.value();
            case DialogAction.CustomClickAction value -> ClickEvent.custom(value.id(), value.additions() == null ? BinaryTagHolder.binaryTagHolder("{}") : value.additions());
            case null, default -> null;
        };
    }

    public void serialize(@NotNull Section section) {
        Section target = section.createSection("click");
        for (String key : this.config.getRoutesAsStrings(true)) {
            if (!this.config.isSection(key)) target.set(key, this.config.get(key));
        }
    }
}
