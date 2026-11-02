package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElementLoader;
import fr.maxlego08.menu.api.dialog.canvas.CanvasLayoutable;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Requirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

/**
 * An element drawn only for some players, from the keys every element accepts:
 *
 * <pre>
 * vip_badge:
 *   type: image
 *   glyph: shop/vip
 *   x: 10
 *   y: 4
 *   visible: "%player_has_permission_shop.vip%"   # drawn when it gives true, yes or 1, see VisibleCondition
 *   # visible: "%canvas_tab% == shop"            # or a comparison
 *   view-requirement: { ... }                     # zMenu requirements, as on inventory buttons
 * </pre>
 * The pack still holds the element for every player, since it cannot depend on one: only the drawing and the
 * click area are skipped. Inside a {@code row} or {@code column}, a hidden element keeps its space unless it
 * sets {@code collapse: true}.
 */
public final class ConditionalElement implements CanvasElement, CanvasLayoutable {

    private final CanvasElement element;
    private final VisibleCondition visible;
    private final Requirement viewRequirement;

    public ConditionalElement(@NotNull CanvasElement element, @Nullable VisibleCondition visible, @Nullable Requirement viewRequirement) {
        this.element = element;
        this.visible = visible;
        this.viewRequirement = viewRequirement;
    }

    /**
     * Loads an element with its loader, wrapped when it sets {@code visible} or {@code view-requirement}. Both
     * keys are taken out of the section first, so element loaders never see them.
     *
     * @param plugin  The plugin.
     * @param loader  The loader of the element's type.
     * @param name    The name of the element.
     * @param section Its section.
     * @param source  Where it is read from, for error messages.
     * @return The element.
     */
    public static @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull CanvasElementLoader loader, @NotNull String name,
                                              @NotNull ConfigurationSection section, @NotNull String source) {
        VisibleCondition visible = section.contains("visible") ? VisibleCondition.parse(String.valueOf(section.get("visible"))) : null;
        Requirement viewRequirement = section.isConfigurationSection("view-requirement") ? readRequirement(plugin, name, section, source) : null;
        section.set("visible", null);
        section.set("view-requirement", null);

        CanvasElement element = loader.load(plugin, name, section, source);
        return visible == null && viewRequirement == null ? element : new ConditionalElement(element, visible, viewRequirement);
    }

    private static @NotNull Requirement readRequirement(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section,
                                                        @NotNull String source) {
        if (!(section.getRoot() instanceof YamlConfiguration configuration) || section.getCurrentPath() == null) {
            throw ElementSections.error(name, source, "must be read from a YAML file to use view-requirement");
        }
        try {
            return plugin.getButtonManager().loadRequirement(configuration, section.getCurrentPath() + ".view-requirement.", new File(source));
        } catch (InventoryException exception) {
            throw ElementSections.error(name, source, "has an invalid view-requirement: " + exception.getMessage());
        }
    }

    /**
     * @param element An element.
     * @return The element without its visibility condition.
     */
    public static @NotNull CanvasElement unwrap(@NotNull CanvasElement element) {
        return element instanceof ConditionalElement conditional ? conditional.element : element;
    }

    /**
     * @param element An element.
     * @param context The player.
     * @return True if the element is drawn for the player.
     */
    public static boolean isVisible(@NotNull Object element, @NotNull CanvasRenderContext context) {
        return !(element instanceof ConditionalElement conditional) || conditional.isVisible(context);
    }

    /**
     * @param context The player.
     * @return True if the element is drawn for the player.
     */
    public boolean isVisible(@NotNull CanvasRenderContext context) {
        if (this.visible != null && !this.visible.test(context)) return false;
        return this.viewRequirement == null || this.viewRequirement.execute(context.player(), null, context.engine(), context.placeholders());
    }

    /**
     * @return The element drawn when the condition holds.
     */
    public @NotNull CanvasElement element() {
        return this.element;
    }

    @Override
    public @NotNull String name() {
        return this.element.name();
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        this.element.collectVariants(variants);
    }

    @Override
    public boolean isClickable() {
        return this.element.isClickable();
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        if (this.isVisible(context)) this.element.render(context, builder);
    }

    @Override
    public boolean hasOverlay() {
        return this.element.hasOverlay();
    }

    @Override
    public void renderOverlay(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        if (this.isVisible(context)) this.element.renderOverlay(context, builder);
    }

    @Override
    public int preferredWidth(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.element instanceof CanvasLayoutable layoutable ? layoutable.preferredWidth(glyphs) : 0;
    }

    @Override
    public int preferredHeight(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.element instanceof CanvasLayoutable layoutable ? layoutable.preferredHeight(glyphs) : 0;
    }

    @Override
    public @NotNull CanvasElement placed(int x, int y, int width, int height) {
        if (!(this.element instanceof CanvasLayoutable layoutable)) return this;
        return new ConditionalElement(layoutable.placed(x, y, width, height), this.visible, this.viewRequirement);
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.element.serialize(section);
        if (this.visible != null) section.set("visible", this.visible.raw());
        if (this.viewRequirement != null) this.viewRequirement.serialize(section.createSection("view-requirement"));
    }
}
