package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.hooks.dialogs.canvas.glyph.PanelGlyphProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A window drawn over the canvas while its key is {@code true}.
 *
 * <pre>
 * confirm:
 *   type: modal
 *   x: 50
 *   y: 20
 *   width: 160
 *   height: 70
 *   panel: panels/frame          # panel texture of the window
 *   border: 4
 *   dim: widgets/dim             # optional texture stretched over the whole canvas, under the window
 *   close-on-outside: true       # a click outside the window closes it (true by default)
 *   elements:                    # any elements, at canvas positions, drawn over everything
 *     question: { type: text, text: "Buy it?", x: 60, y: 30 }
 *     yes: { type: button, ..., actions: [ { type: canvas-set, key: confirm, value: "false" } ], refresh: true }
 * </pre>
 * Open it with {@code canvas-set} ({@code key: confirm}, {@code value: "true"}) and {@code refresh: true} on a
 * button. Nothing behind the window takes a click while it is open. Plus the keys of {@link WidgetSettings}.
 */
public final class ModalWidget extends WidgetElement {

    public static final String TYPE = "modal";

    private final int width;
    private final int height;
    private final String panel;
    private final int border;
    private final String dim;
    private final boolean closeOnOutside;
    private final List<CanvasElement> elements;

    public ModalWidget(@NotNull WidgetSettings settings, int width, int height, @NotNull String panel, int border, @Nullable String dim, boolean closeOnOutside,
                       @NotNull List<CanvasElement> elements) {
        super(settings);
        this.width = width;
        this.height = height;
        this.panel = panel;
        this.border = border;
        this.dim = dim;
        this.closeOnOutside = closeOnOutside;
        this.elements = List.copyOf(elements);
    }

    /**
     * @return The elements of the window.
     */
    public @NotNull List<CanvasElement> elements() {
        return this.elements;
    }

    private boolean isOpen(@NotNull CanvasRenderContext context) {
        return ToggleWidget.isOn(this.value(context));
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        variants.beginOverlay();
        if (this.dim != null && variants.canvasWidth() > 0 && variants.canvasHeight() > 0) {
            this.glyph(variants, PanelGlyphProvider.reference(this.dim, variants.canvasWidth(), variants.canvasHeight(), 0), 0);
        }
        coverCanvas(variants);
        this.glyph(variants, PanelGlyphProvider.reference(this.panel, this.width, this.height, this.border), this.settings.y());
        for (CanvasElement element : this.elements) element.collectVariants(variants);
        variants.endOverlay();
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
    }

    @Override
    public boolean hasOverlay() {
        return true;
    }

    @Override
    public void renderOverlay(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        if (!this.isOpen(context)) return;
        if (this.dim != null) this.glyph(context, builder, PanelGlyphProvider.reference(this.dim, builder.width(), builder.height(), 0), 0, 0);
        if (this.closeOnOutside) {
            Map<String, String> close = new LinkedHashMap<>();
            close.put(this.settings.key(), "false");
            coverCanvas(builder, this.update(context, close, "false"));
        } else {
            coverCanvas(builder, nothing(context));
        }
        this.glyph(context, builder, PanelGlyphProvider.reference(this.panel, this.width, this.height, this.border), this.settings.x(), this.settings.y());
        for (CanvasElement element : this.elements) element.render(context, builder);
        for (CanvasElement element : this.elements) {
            if (element.hasOverlay()) element.renderOverlay(context, builder);
        }
    }

    @Override
    public @NotNull List<String> problems(int canvasWidth, int canvasHeight) {
        return outside(this.settings.x() + this.width, this.settings.y() + this.height, canvasWidth, canvasHeight, "the window");
    }

    @Override
    public @NotNull List<CanvasElement> children() {
        return this.elements;
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section, TYPE, "false");
        section.set("width", this.width);
        section.set("height", this.height);
        section.set("panel", this.panel);
        section.set("border", this.border);
        if (this.dim != null) section.set("dim", this.dim);
        if (!this.closeOnOutside) section.set("close-on-outside", false);
        ConfigurationSection elementsSection = section.createSection("elements");
        for (CanvasElement element : this.elements) element.serialize(elementsSection.createSection(element.name()));
    }
}
