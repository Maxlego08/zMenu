package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.hooks.dialogs.canvas.state.CanvasStateAction;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A widget showing one page out of several, with previous and next buttons. Its value is the page number,
 * from 1, and {@code %page%} and {@code %pages%} are given to its texts and elements.
 */
public abstract class PagedWidget extends WidgetElement {

    protected final PageNavigation navigation;

    protected PagedWidget(@NotNull WidgetSettings settings, @NotNull PageNavigation navigation) {
        super(settings);
        this.navigation = navigation;
    }

    /**
     * @param value The value of the widget.
     * @param pages The number of pages.
     * @return The page shown, from 0.
     */
    public static int page(@NotNull String value, int pages) {
        return Math.clamp((int) CanvasStateAction.number(value, 1) - 1, 0, Math.max(0, pages - 1));
    }

    /**
     * A context whose placeholders add {@code %page%} and {@code %pages%}.
     *
     * @param context The player.
     * @param page    The page shown, from 0.
     * @param pages   The number of pages.
     * @return The context.
     */
    protected static @NotNull CanvasRenderContext pageContext(@NotNull CanvasRenderContext context, int page, int pages) {
        return context.with(Map.of("page", String.valueOf(page + 1), "pages", String.valueOf(pages)));
    }

    /**
     * Draws the previous and next buttons and the page text.
     *
     * @param context The player, with the page placeholders.
     * @param builder The canvas.
     * @param page    The page shown, from 0.
     * @param pages   The number of pages.
     */
    protected void renderNavigation(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder, int page, int pages) {
        this.button(context, builder, this.navigation.previous(), page > 0, page);
        this.button(context, builder, this.navigation.next(), page < pages - 1 && this.canGoNext(context, page), page + 2);
        PageNavigation.PageText pageText = this.navigation.pageText();
        if (pageText == null) return;
        if (pageText.width() > 0) {
            builder.text(context.format(pageText.text(), this.settings.escapePlaceholders()), pageText.font(), pageText.x(), pageText.y(), pageText.width(),
                    pageText.align());
        } else {
            builder.text(context.format(pageText.text(), this.settings.escapePlaceholders()), pageText.font(), pageText.x(), pageText.y());
        }
    }

    /**
     * @param context The player, with the page placeholders.
     * @param page    The page shown, from 0.
     * @return True if the next button takes clicks on this page.
     */
    protected boolean canGoNext(@NotNull CanvasRenderContext context, int page) {
        return true;
    }

    private void button(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder, @NotNull PageNavigation.Button button, boolean enabled,
                        int target) {
        String glyph = enabled ? button.glyph() : button.glyphDisabled();
        this.glyph(context, builder, glyph, button.x(), button.y());
        if (!enabled) return;
        if (button.text() != null) {
            builder.text(context.format(button.text(), this.settings.escapePlaceholders()), button.font(), button.x(), button.textY(), button.width(),
                    HAlign.CENTER);
        }
        builder.area(button.x(), button.y(), button.width(), button.height(), this.change(context, String.valueOf(target)), null);
    }

    /**
     * Declares the navigation for the pack.
     *
     * @param variants The collector.
     */
    protected void collectNavigation(@NotNull CanvasVariants variants) {
        this.navigation.collectVariants(variants);
    }
}
