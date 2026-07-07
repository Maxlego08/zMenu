package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public final class ResolvableComponent extends ParsableResolvable<Component> {
    private final PaperMetaUpdater paperMetaUpdater;

    private ResolvableComponent(@Nullable Component resolvedValue, @Nullable String expression, @NotNull PaperMetaUpdater paperMetaUpdater) {
        super(resolvedValue, expression);
        this.paperMetaUpdater = paperMetaUpdater;
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

    @Override
    protected @Nullable Component parse(@NotNull String value) {
        return this.paperMetaUpdater.getComponent(value);
    }
}
