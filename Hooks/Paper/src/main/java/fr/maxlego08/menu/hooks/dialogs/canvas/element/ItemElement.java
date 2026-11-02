package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.MenuItemStack;
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
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * An item: its icon, its amount, and its real tooltip on hover (name, lore, enchantments...), built with the
 * zMenu item format for each player.
 *
 * <pre>
 * sword:
 *   type: item
 *   x: 10
 *   y: 20
 *   item:                          # the zMenu item format, as in inventories
 *     material: DIAMOND_SWORD
 *     name: "&lt;aqua&gt;Sword of %player_name%"
 *     lore: ["&lt;gray&gt;Sharp"]
 *   glyph: minecraft:item/diamond_sword   # optional: minecraft:item/&lt;material&gt; by default
 *   height: 16                     # optional, the height of the icon
 *   show-amount: true              # optional, the amount at the bottom right when above 1
 *   actions: [ ... ]               # optional, like a button: click-requirement, refresh
 *   click: { ... }                 # optional, like a button, instead of actions: see {@link ClickValue}
 * </pre>
 * A canvas cannot draw an item model, so the icon is a glyph: vanilla item textures by default, any glyph
 * otherwise (a block, a Nexo or ItemsAdder glyph...). The tooltip is the item's own.
 */
public final class ItemElement implements CanvasElement, CanvasLayoutable {

    private static final int TEXT_HEIGHT = 8;

    private final String name;
    private final PositionValue x;
    private final PositionValue y;
    private final MenuItemStack item;
    private final PackValue glyph;
    private final Integer height;
    private final boolean showAmount;
    private final List<Action> actions;
    private final List<Requirement> clickRequirements;
    private final boolean refresh;
    private final ClickValue click;

    public ItemElement(@NotNull String name, @NotNull PositionValue x, @NotNull PositionValue y, @NotNull MenuItemStack item, @NotNull PackValue glyph,
                       @Nullable Integer height, boolean showAmount, @NotNull List<Action> actions, @NotNull List<Requirement> clickRequirements, boolean refresh,
                       @Nullable ClickValue click) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.item = item;
        this.glyph = glyph;
        this.height = height;
        this.showAmount = showAmount;
        this.actions = List.copyOf(actions);
        this.clickRequirements = List.copyOf(clickRequirements);
        this.refresh = refresh;
        this.click = click;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    private @NotNull ResolvedGlyph scaled(@NotNull ResolvedGlyph glyph) {
        return this.height == null ? glyph : glyph.withHeight(this.height);
    }

    private @NotNull Optional<ResolvedGlyph> icon(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs, @NotNull String reference) {
        return glyphs.apply(reference).map(this::scaled);
    }

    @Override
    public int preferredWidth(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.icon(glyphs, this.glyph.fallback()).map(ResolvedGlyph::visualWidth).orElse(16);
    }

    @Override
    public int preferredHeight(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.icon(glyphs, this.glyph.fallback()).map(ResolvedGlyph::height).orElse(16);
    }

    @Override
    public @NotNull CanvasElement placed(int x, int y, int width, int height) {
        return new ItemElement(this.name, PositionValue.of(x), PositionValue.of(y), this.item, this.glyph, this.height, this.showAmount, this.actions,
                this.clickRequirements, this.refresh, this.click);
    }

    @Override
    public boolean isClickable() {
        return this.click != null || !this.actions.isEmpty() || !this.clickRequirements.isEmpty() || this.refresh;
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        for (String reference : this.glyph.values()) {
            ResolvedGlyph resolved = variants.resolve(reference);
            if (resolved == null) continue;
            ResolvedGlyph icon = this.scaled(resolved);
            for (int top : this.y.all()) {
                variants.glyph(icon, top);
                if (this.showAmount) variants.text("default", top + Math.max(0, icon.height() - TEXT_HEIGHT));
                variants.area(top, icon.visualWidth(), icon.height());
            }
        }
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        Optional<ResolvedGlyph> icon = this.icon(context.canvasManager()::resolveGlyph, this.glyph.resolve(context));
        if (icon.isEmpty()) return;
        int left = this.x.resolve(context);
        int top = this.y.resolve(context);
        int width = icon.get().visualWidth();
        int height = icon.get().height();
        builder.glyph(icon.get(), left, top);

        ItemStack stack = this.item.build(context.player(), false, context.placeholders());
        if (this.showAmount && stack != null && stack.getAmount() > 1) {
            builder.text(Component.text(stack.getAmount()), "default", left, top + Math.max(0, height - TEXT_HEIGHT), width, HAlign.RIGHT);
        }
        ClickEvent click;
        if (this.click != null) click = this.click.clickEvent(context);
        else click = this.isClickable() ? context.canvasManager().callback(context.player(), clicker -> this.click(context, clicker)) : null;
        builder.area(left, top, width, height, click, stack == null || stack.getType().isAir() ? null : stack);
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
        section.set("type", "item");
        this.x.serialize(section, "x");
        this.y.serialize(section, "y");
        this.item.serialize(section.createSection("item"));
        this.glyph.serialize(section, "glyph");
        if (this.height != null) section.set("height", this.height);
        if (!this.showAmount) section.set("show-amount", false);
        if (!this.clickRequirements.isEmpty()) {
            ConfigurationSection requirements = section.createSection("click-requirement");
            for (int index = 0; index < this.clickRequirements.size(); index++) {
                this.clickRequirements.get(index).serialize(requirements.createSection(String.valueOf(index + 1)));
            }
        }
        if (!this.actions.isEmpty()) section.set("actions", this.actions.stream().map(Action::serialize).toList());
        if (this.refresh) section.set("refresh", true);
        if (this.click != null) this.click.serialize(section);
    }
}
