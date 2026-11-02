package fr.maxlego08.menu.api.button.buttons.dialogs.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.*;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A dialog body drawing images and text at pixel positions, type {@code dialog-canvas}.
 * <p>
 * Its {@code width} and {@code height} may be placeholders: neither changes the resource pack, they only
 * set the width of the body and its number of lines. Players who cannot see the canvas, because they have
 * not loaded a resource pack, get the {@code fallback} lines as a plain message instead.
 * <p>
 * A canvas whose size and elements are all {@linkplain CanvasElement#isStatic() static} is drawn once and
 * reused for every player until the resource pack is generated again.
 */
public class DialogCanvasBody extends VanillaDialogBody {

    private final CanvasValue<Integer> canvasWidth;
    private final CanvasValue<Integer> canvasHeight;
    private final List<CanvasElement> elements;
    private final boolean[] overlays;
    private final List<String> fallback;
    private final boolean isStatic;
    private volatile Rendered rendered;
    private volatile Lines lines;

    public DialogCanvasBody(@NotNull CanvasValue<Integer> canvasWidth, @NotNull CanvasValue<Integer> canvasHeight, @NotNull List<CanvasElement> elements, @NotNull List<String> fallback) {
        super(DialogBodyType.PLAIN_MESSAGE, canvasWidth.fixedOrFallback());
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.elements = List.copyOf(elements);
        this.overlays = new boolean[this.elements.size()];
        for (int index = 0; index < this.overlays.length; index++) this.overlays[index] = this.elements.get(index).hasOverlay();
        this.fallback = List.copyOf(fallback);
        this.isStatic = !canvasWidth.isPlaceholder() && !canvasHeight.isPlaceholder() && this.elements.stream().allMatch(CanvasElement::isStatic);
    }

    /**
     * @return The elements, in drawing order.
     */
    public @NotNull List<CanvasElement> getElements() {
        return this.elements;
    }

    /**
     * @return True if an element of this canvas reacts to clicks.
     */
    public boolean hasClickableElements() {
        return this.elements.stream().anyMatch(CanvasElement::isClickable);
    }

    public @NotNull CanvasValue<Integer> getCanvasWidth() {
        return this.canvasWidth;
    }

    public @NotNull CanvasValue<Integer> getCanvasHeight() {
        return this.canvasHeight;
    }

    @Override
    public DialogBody build(@NotNull DialogRenderContext<DialogBody, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        MenuPlugin plugin = context.getPlugin();
        Player player = context.getPlayer();
        CanvasManager canvasManager = plugin.getCanvasManager();
        if (canvasManager == null || !canvasManager.canSeeCanvas(player)) {
            return this.buildFallback(context);
        }

        GeneratedPack pack = canvasManager.getGeneratedPack().orElse(null);
        Rendered rendered = this.rendered;
        if (this.isStatic && pack != null && rendered != null && rendered.pack() == pack) return rendered.body();

        CanvasRenderContext renderContext = new CanvasRenderContext(plugin, canvasManager, player, plugin.getInventoryManager().getFakeInventory(), context.getMetaUpdater(), context.getPlaceholders(), context.getInventory());
        CanvasBuilder builder = canvasManager.newCanvas(this.canvasWidth.resolve(renderContext), this.canvasHeight.resolve(renderContext), player);
        Lines lines = this.lines;
        boolean useLines = lines != null && pack != null && lines.pack() == pack && lines.base().length == this.elements.size();
        for (int index = 0; index < this.elements.size(); index++) {
            builder.line(useLines ? lines.base()[index] : 0);
            this.elements.get(index).render(renderContext, builder);
        }
        for (int index = 0; index < this.elements.size(); index++) {
            if (!this.overlays[index]) continue;
            builder.line(useLines ? lines.overlay()[index] : 0);
            this.elements.get(index).renderOverlay(renderContext, builder);
        }
        DialogBody body = DialogBody.plainMessage(builder.build(), builder.bodyWidth());
        if (this.isStatic && pack != null) this.rendered = new Rendered(pack, body);
        return body;
    }

    /**
     * @return True if this canvas is drawn once and reused for every player, until the pack is generated again.
     */
    public boolean isStatic() {
        return this.isStatic;
    }

    private record Rendered(@NotNull GeneratedPack pack, @NotNull DialogBody body) {
    }

    private record Lines(@NotNull GeneratedPack pack, int @NotNull [] base, int @NotNull [] overlay) {
    }

    /**
     * Sets the text line of each element, as the pack was generated: the canvas puts each element on its
     * line before drawing it (see {@link CanvasBuilder#line(int)}). Until it is called, or for another pack,
     * every element is drawn on the first line.
     *
     * @param pack    The pack the lines were generated with.
     * @param base    The line of each element, in the order of the elements.
     * @param overlay The line of the overlay of each element.
     */
    public void useLines(@NotNull GeneratedPack pack, int @NotNull [] base, int @NotNull [] overlay) {
        this.lines = new Lines(pack, base.clone(), overlay.clone());
    }

    private DialogBody buildFallback(@NotNull DialogRenderContext<DialogBody, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        if (this.fallback.isEmpty()) return null;
        Placeholders placeholders = context.getPlaceholders();
        List<Component> lines = new ArrayList<>(this.fallback.size());
        for (String line : this.fallback) {
            lines.add(context.getMetaUpdater().getComponent(context.getPlugin().parse(context.getPlayer(), placeholders.parse(line))));
        }
        return DialogBody.plainMessage(Component.join(JoinConfiguration.newlines(), lines), Math.min(1024, this.canvasWidth.fixedOrFallback() + 10));
    }

    @Override
    protected void serializeProperties(@NotNull ConfigurationSection section) {
        section.set("type", "dialog-canvas");
        this.canvasWidth.serialize(section, "width");
        this.canvasHeight.serialize(section, "height");
        if (!this.fallback.isEmpty()) section.set("fallback", this.fallback);
        ConfigurationSection elementsSection = section.createSection("elements");
        for (CanvasElement element : this.elements) element.serialize(elementsSection.createSection(element.name()));
    }
}
