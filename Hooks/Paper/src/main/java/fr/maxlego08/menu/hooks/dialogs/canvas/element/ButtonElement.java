package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasLayoutable;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A clickable area, with an optional image and label.
 *
 * <pre>
 * buy:
 *   type: button
 *   x: 20
 *   y: 60
 *   width: 80
 *   height: 18
 *   glyph: nexo:shop_button     # optional, drawn at x and y; a placeholder needs glyph-values
 *   text: "Buy"                 # optional, centred in the button
 *   font: default               # optional
 *   hover:                      # optional tooltip
 *     - "&lt;gray&gt;Costs 100 coins"
 *   view-requirement: { ... }   # optional, hides the button (every element accepts it)
 *   click-requirement: { ... }  # optional, checked before the actions
 *   actions:                    # zMenu actions
 *     - type: message
 *       messages: [ "Bought!" ]
 *   refresh: true               # optional, opens the dialog again after the actions
 *   escape-placeholders: true   # optional: false reads the tags placeholders give in text and hover
 * </pre>
 * Instead of {@code actions}, {@code click-requirement} and {@code refresh}, a button can take a {@code click}
 * written like the action of a dialog button, to open a link, run or suggest a command or copy a text, see
 * {@link ClickValue}.
 * {@code x} and {@code y} accept placeholders, see {@link PositionValue}.
 */
public final class ButtonElement implements CanvasElement, CanvasLayoutable {

    private final String name;
    private final PositionValue x;
    private final PositionValue y;
    private final int width;
    private final int height;
    private final PackValue glyph;
    private final String text;
    private final PackValue font;
    private final List<String> hover;
    private final List<Action> actions;
    private final List<Requirement> clickRequirements;
    private final boolean refresh;
    private final ClickValue click;
    private final boolean escapePlaceholders;

    public ButtonElement(@NotNull String name, @NotNull PositionValue x, @NotNull PositionValue y, int width, int height, @Nullable PackValue glyph, @Nullable String text, @NotNull PackValue font,
                         @NotNull List<String> hover, @NotNull List<Action> actions, @NotNull List<Requirement> clickRequirements,
                         boolean refresh, @Nullable ClickValue click, boolean escapePlaceholders) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.glyph = glyph;
        this.text = text;
        this.font = font;
        this.hover = List.copyOf(hover);
        this.actions = List.copyOf(actions);
        this.clickRequirements = List.copyOf(clickRequirements);
        this.refresh = refresh;
        this.click = click;
        this.escapePlaceholders = escapePlaceholders;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public int preferredWidth(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.width;
    }

    @Override
    public int preferredHeight(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.height;
    }

    @Override
    public @NotNull CanvasElement placed(int x, int y, int width, int height) {
        return new ButtonElement(this.name, PositionValue.of(x), PositionValue.of(y), Math.max(1, width), Math.clamp(height, 1, 256), this.glyph, this.text,
                this.font, this.hover, this.actions, this.clickRequirements, this.refresh, this.click, this.escapePlaceholders);
    }

    private int textOffset() {
        return Math.max(0, (this.height - 8) / 2);
    }

    @Override
    public boolean isClickable() {
        return true;
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        List<ResolvedGlyph> resolved = new ArrayList<>();
        if (this.glyph != null) {
            for (String reference : this.glyph.values()) {
                ResolvedGlyph value = variants.resolve(reference);
                if (value != null) resolved.add(value);
            }
        }
        for (int top : this.y.all()) {
            for (ResolvedGlyph value : resolved) variants.glyph(value, top);
            if (this.text != null) {
                for (String font : this.font.values()) variants.text(font, top + this.textOffset());
            }
            variants.area(top, this.width, this.height);
        }
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        Player player = context.player();

        int left = this.x.resolve(context);
        int top = this.y.resolve(context);
        if (this.glyph != null) {
            context.canvasManager().resolveGlyph(this.glyph.resolve(context)).ifPresent(resolved -> builder.glyph(resolved, left, top));
        }
        if (this.text != null) {
            builder.text(context.format(this.text, this.escapePlaceholders), this.font.resolve(context), left, top + this.textOffset(), this.width, HAlign.CENTER);
        }

        Component tooltip = null;
        if (!this.hover.isEmpty()) {
            List<Component> lines = new ArrayList<>(this.hover.size());
            for (String line : this.hover) lines.add(context.format(line, this.escapePlaceholders));
            tooltip = Component.join(JoinConfiguration.newlines(), lines);
        }
        boolean clickable = !this.actions.isEmpty() || !this.clickRequirements.isEmpty() || this.refresh;
        ClickEvent click;
        if (this.click != null) click = this.click.clickEvent(context);
        else click = clickable ? context.canvasManager().callback(player, clicker -> this.click(context, clicker)) : null;
        builder.area(left, top, this.width, this.height, click, tooltip);
    }

    private void click(@NotNull CanvasRenderContext context, @NotNull Player player) {
        Placeholders placeholders = context.placeholders();
        for (Requirement requirement : this.clickRequirements) {
            if (!requirement.execute(player, null, context.engine(), placeholders)) return;
        }
        for (Action action : this.actions) action.preExecute(player, null, context.engine(), placeholders);

        DialogInventory dialog = context.dialog();
        if (this.refresh && dialog != null) context.canvasManager().reopen(player, dialog.getPlugin().getName() + ":" + dialog.getFileName());
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", "button");
        this.x.serialize(section, "x");
        this.y.serialize(section, "y");
        section.set("width", this.width);
        section.set("height", this.height);
        if (this.glyph != null) this.glyph.serialize(section, "glyph");
        if (this.text != null) section.set("text", this.text);
        if (!this.font.raw().equals("default")) this.font.serialize(section, "font");
        if (!this.hover.isEmpty()) section.set("hover", this.hover);
        if (!this.clickRequirements.isEmpty()) {
            ConfigurationSection requirements = section.createSection("click-requirement");
            for (int index = 0; index < this.clickRequirements.size(); index++) {
                this.clickRequirements.get(index).serialize(requirements.createSection(String.valueOf(index + 1)));
            }
        }
        if (!this.actions.isEmpty()) section.set("actions", this.actions.stream().map(Action::serialize).toList());
        if (this.refresh) section.set("refresh", true);
        if (this.click != null) this.click.serialize(section);
        if (!this.escapePlaceholders) section.set("escape-placeholders", false);
    }
}
