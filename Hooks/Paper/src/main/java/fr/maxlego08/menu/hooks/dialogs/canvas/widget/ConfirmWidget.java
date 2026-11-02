package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A button that asks for a second click: the first click arms it (its value becomes {@code true} and it
 * shows its armed look), the second runs its actions and disarms it.
 *
 * <pre>
 * delete:
 *   type: confirm
 *   x: 10
 *   y: 60
 *   width: 80
 *   height: 14
 *   glyph: widgets/minus                 # optional
 *   glyph-armed: widgets/minus_off       # optional, the glyph once armed (glyph by default)
 *   text: "&lt;red&gt;Delete"
 *   text-armed: "&lt;dark_red&gt;Sure?"         # optional, the text once armed
 *   click-requirement: { ... }           # optional, checked on the second click
 *   actions:                             # run on the second click
 *     - type: message
 *       messages: ["Deleted"]
 *   refresh: true                        # opens the dialog again after the actions (default true),
 *                                        # false when an action opens something else
 * </pre>
 * {@code text} and {@code text-armed} are parts (see {@link WidgetPart}): moved relative to the button, each
 * with its own {@code hover}. Plus the keys of {@link WidgetSettings}: {@code on-change} runs when it is armed.
 */
public final class ConfirmWidget extends WidgetElement {

    public static final String TYPE = "confirm";
    private static final int TEXT_HEIGHT = 8;

    private final int width;
    private final int height;
    private final String glyph;
    private final String glyphArmed;
    private final WidgetPart text;
    private final WidgetPart textArmed;
    private final String font;
    private final List<Action> actions;
    private final List<Requirement> clickRequirements;
    private final boolean refresh;

    public ConfirmWidget(@NotNull WidgetSettings settings, int width, int height, @Nullable String glyph, @Nullable String glyphArmed, @Nullable String text,
                         @Nullable String textArmed, @NotNull String font, @NotNull List<Action> actions, @NotNull List<Requirement> clickRequirements,
                         boolean refresh) {
        this(settings, width, height, glyph, glyphArmed, WidgetPart.ofText(text), WidgetPart.ofText(textArmed), font, actions, clickRequirements, refresh);
    }

    public ConfirmWidget(@NotNull WidgetSettings settings, int width, int height, @Nullable String glyph, @Nullable String glyphArmed,
                         @NotNull WidgetPart text, @NotNull WidgetPart textArmed, @NotNull String font, @NotNull List<Action> actions,
                         @NotNull List<Requirement> clickRequirements, boolean refresh) {
        super(settings);
        this.width = width;
        this.height = height;
        this.glyph = glyph;
        this.glyphArmed = glyphArmed == null ? glyph : glyphArmed;
        this.text = text;
        this.textArmed = textArmed;
        this.font = font;
        this.actions = List.copyOf(actions);
        this.clickRequirements = List.copyOf(clickRequirements);
        this.refresh = refresh;
    }

    private int textY() {
        return Math.max(0, (this.height - TEXT_HEIGHT) / 2);
    }

    private @NotNull WidgetPart textFor(boolean armed) {
        return armed ? this.textArmed.orElse(this.text) : this.text;
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        this.glyph(variants, this.glyph, this.settings.y());
        if (this.glyphArmed != null && !this.glyphArmed.equals(this.glyph)) this.glyph(variants, this.glyphArmed, this.settings.y());
        collectTexts(variants, List.of(this.textFor(false), this.textFor(true)), null, this.settings.y(), this.textY(), this.font);
        variants.area(this.settings.y(), this.width, this.height);
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        String value = this.value(context);
        boolean armed = ToggleWidget.isOn(value);
        int x = this.settings.x();
        int y = this.settings.y();

        this.glyph(context, builder, armed ? this.glyphArmed : this.glyph, x, y);
        WidgetPart part = this.textFor(armed);
        this.drawText(context, builder, part, null, value, x, y, 0, this.textY(), this.width, HAlign.CENTER, this.font);

        ClickEvent click = armed ? this.confirm(context) : this.change(context, "true");
        builder.area(x, y, this.width, this.height, click, this.hover(context, value, part));
    }

    private @Nullable ClickEvent confirm(@NotNull CanvasRenderContext context) {
        if (!this.isEnabled(context)) return null;
        return context.canvasManager().callback(context.player(), player -> this.run(context, player));
    }

    private void run(@NotNull CanvasRenderContext context, @NotNull Player player) {
        Placeholders placeholders = context.placeholders();
        for (Requirement requirement : this.clickRequirements) {
            if (!requirement.execute(player, null, context.engine(), placeholders)) return;
        }
        DialogInventory dialog = context.dialog();
        if (dialog != null) context.canvasManager().getStateService().remove(player.getUniqueId(), dialog.getFileName(), this.settings.key());
        for (Action action : this.actions) action.preExecute(player, null, context.engine(), placeholders);
        if (this.refresh && dialog != null) context.canvasManager().reopen(player, dialog.getPlugin().getName() + ":" + dialog.getFileName());
    }

    @Override
    public @NotNull List<String> problems(int canvasWidth, int canvasHeight) {
        return outside(this.settings.x() + this.width, this.settings.y() + this.height, canvasWidth, canvasHeight, "the button");
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section, TYPE, "false");
        section.set("width", this.width);
        section.set("height", this.height);
        if (this.glyph != null) section.set("glyph", this.glyph);
        if (this.glyphArmed != null && !this.glyphArmed.equals(this.glyph)) section.set("glyph-armed", this.glyphArmed);
        this.text.serialize(section, "text", false);
        this.textArmed.serialize(section, "text-armed", false);
        if (!this.font.equals("default")) section.set("font", this.font);
        if (!this.clickRequirements.isEmpty()) {
            ConfigurationSection requirements = section.createSection("click-requirement");
            for (int index = 0; index < this.clickRequirements.size(); index++) {
                this.clickRequirements.get(index).serialize(requirements.createSection(String.valueOf(index + 1)));
            }
        }
        if (!this.actions.isEmpty()) section.set("actions", this.actions.stream().map(Action::serialize).toList());
        if (!this.refresh) section.set("refresh", false);
    }
}
