package fr.maxlego08.menu.api.text;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.robie.minimessageflow.MiniMessageFlow;
import fr.robie.minimessageflow.NumberStyle;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A {@link TextService} over an instance built by a subclass: the extra tags and the configuration applied to it.
 */
public abstract class AbstractTextService implements TextService {

    private final MiniMessageFlow flow;
    /**
     * The extra tags given by {@link #tags}, to take them out again on the next call.
     */
    private final List<TagResolver> tags = new ArrayList<>();

    protected AbstractTextService(@NotNull MiniMessageFlow flow) {
        this.flow = flow;
    }

    @Override
    public @NotNull MiniMessageFlow flow() {
        return this.flow;
    }

    @Override
    public synchronized void tags(@NotNull List<TagResolver> tags) {
        for (TagResolver previous : this.tags) {
            this.flow.removeTags(previous);
        }
        this.tags.clear();
        for (TagResolver resolver : tags) {
            this.flow.tags(resolver);
            this.tags.add(resolver);
        }
    }

    @Override
    public void configure() {
        this.flow.numberStyle(numberStyle(Configuration.numberStyle));
        this.flow.clearCache();
    }

    /**
     * @return the number style of a {@code number-style} value, {@code auto} for an unknown one
     */
    protected static @NotNull NumberStyle numberStyle(@Nullable String name) {
        if (name == null || name.isBlank()) {
            return NumberStyle.AUTO;
        }
        try {
            return NumberStyle.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return NumberStyle.AUTO;
        }
    }
}
