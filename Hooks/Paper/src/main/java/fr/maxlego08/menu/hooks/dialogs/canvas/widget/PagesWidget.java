package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.VisibleCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pages of elements shown one at a time, the closest thing to scrolling a canvas can do.
 *
 * <pre>
 * rules:
 *   type: pages
 *   pages:                        # one section per page, in order; the names are free
 *     one:
 *       rule: { type: text, text: "Rule 1 ...", x: 10, y: 20 }
 *     two:
 *       rule: { type: text, text: "Rule 2 ...", x: 10, y: 20 }
 *   previous: { x: 10, y: 80, text: "&lt;gray&gt;« Back" , width: 40 }
 *   next: { x: 170, y: 80, text: "&lt;gray&gt;Next »", width: 40 }
 *   page-text: { x: 0, y: 82, width: 220, align: center }
 *   next-requirement:             # optional, a wizard: by page name, the condition to go to the next page
 *     one: "%canvas_name% != "    # same syntax as visible; until it holds, next shows glyph-disabled
 * </pre>
 * Each page is a section of elements, at canvas positions. The value of the widget is the page, from 1.
 * Plus the keys of {@link WidgetSettings} (its x and y are not used) and {@link PageNavigation}.
 */
public final class PagesWidget extends PagedWidget {

    public static final String TYPE = "pages";

    private final List<String> names;
    private final List<List<CanvasElement>> pages;
    private final Map<String, VisibleCondition> nextRequirements;

    public PagesWidget(@NotNull WidgetSettings settings, @NotNull PageNavigation navigation, @NotNull Map<String, List<CanvasElement>> pages) {
        this(settings, navigation, pages, Map.of());
    }

    public PagesWidget(@NotNull WidgetSettings settings, @NotNull PageNavigation navigation, @NotNull Map<String, List<CanvasElement>> pages,
                       @NotNull Map<String, VisibleCondition> nextRequirements) {
        super(settings, navigation);
        this.names = List.copyOf(pages.keySet());
        this.pages = pages.values().stream().map(List::copyOf).toList();
        this.nextRequirements = new LinkedHashMap<>(nextRequirements);
    }

    @Override
    protected boolean canGoNext(@NotNull CanvasRenderContext context, int page) {
        VisibleCondition requirement = this.nextRequirements.get(this.names.get(page));
        return requirement == null || requirement.test(context);
    }

    /**
     * @return The elements of each page.
     */
    public @NotNull List<List<CanvasElement>> pages() {
        return this.pages;
    }

    @Override
    public @NotNull List<CanvasElement> children() {
        return this.pages.stream().flatMap(List::stream).toList();
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        for (List<CanvasElement> page : this.pages) {
            for (CanvasElement element : page) element.collectVariants(variants);
        }
        this.collectNavigation(variants);
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        int page = page(this.value(context), this.pages.size());
        CanvasRenderContext pageContext = pageContext(context, page, this.pages.size());
        for (CanvasElement element : this.pages.get(page)) element.render(pageContext, builder);
        this.renderNavigation(pageContext, builder, page, this.pages.size());
    }

    @Override
    public boolean hasOverlay() {
        return this.pages.stream().flatMap(List::stream).anyMatch(CanvasElement::hasOverlay);
    }

    @Override
    public void renderOverlay(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        int page = page(this.value(context), this.pages.size());
        CanvasRenderContext pageContext = pageContext(context, page, this.pages.size());
        for (CanvasElement element : this.pages.get(page)) {
            if (element.hasOverlay()) element.renderOverlay(pageContext, builder);
        }
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section, TYPE, "1");
        ConfigurationSection pagesSection = section.createSection("pages");
        for (int index = 0; index < this.pages.size(); index++) {
            ConfigurationSection page = pagesSection.createSection(this.names.get(index));
            for (CanvasElement element : this.pages.get(index)) element.serialize(page.createSection(element.name()));
        }
        if (!this.nextRequirements.isEmpty()) {
            ConfigurationSection requirements = section.createSection("next-requirement");
            this.nextRequirements.forEach((page, requirement) -> requirements.set(page, requirement.raw()));
        }
        this.navigation.serialize(section);
    }
}
