package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import fr.robie.minimessageflow.Template;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public final class ResolvableComponent extends ParsableResolvable<Component> {
    private final PaperMetaUpdater paperMetaUpdater;
    /**
     * The expression compiled once, or null for a fixed component.
     */
    private final transient @Nullable Template template;

    private ResolvableComponent(@Nullable Component resolvedValue, @Nullable String expression, @NotNull PaperMetaUpdater paperMetaUpdater) {
        super(resolvedValue, expression);
        this.paperMetaUpdater = paperMetaUpdater;
        this.template = expression == null ? null : TextService.get().flow().compile(expression);
    }

    @NotNull
    public static ResolvableComponent ofValue(@NotNull Component value, @NotNull PaperMetaUpdater paperMetaUpdater) {
        return new ResolvableComponent(value, null, paperMetaUpdater);
    }

    @NotNull
    public static ResolvableComponent ofExpression(@NotNull String expression, @NotNull PaperMetaUpdater paperMetaUpdater) {
        return new ResolvableComponent(null, expression, paperMetaUpdater);
    }

    @NotNull
    public static ResolvableComponent auto(@NotNull String value, @NotNull PaperMetaUpdater paperMetaUpdater) {
        return auto(value, paperMetaUpdater::getComponent, (component, string) -> new ResolvableComponent(component, string, paperMetaUpdater));
    }

    @Nullable
    public static ResolvableComponent autoOrNull(@Nullable String value, @NotNull PaperMetaUpdater paperMetaUpdater) {
        return autoOrNull(value, paperMetaUpdater::getComponent, (component, string) -> new ResolvableComponent(component, string, paperMetaUpdater));
    }

    /**
     * The fixed component, or the expression, compiled when this was made, rendered with the build's values and
     * PlaceholderAPI for its player.
     */
    @Override
    public @Nullable Component resolve(@NotNull BuildContext context) {
        Template template = this.template;
        if (template == null) {
            return this.getResolvedValue();
        }
        return template.render(context.getPlaceholders().values(), context.getPlayer());
    }

    @Override
    protected @Nullable Component parse(@NotNull String value) {
        return this.paperMetaUpdater.getComponent(value);
    }

    @Override
    protected @NotNull Object serializeValue(@NotNull Component value) {
        return this.paperMetaUpdater.getMiniMessage(value);
    }
}
