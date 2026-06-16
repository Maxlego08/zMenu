package fr.maxlego08.menu.api.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PaperMetaUpdater extends MetaUpdater {
    void withTagResolver(@NotNull TagResolver tagResolver);

    void buildMiniMessage();

    void clearCache();

    @NotNull Component getComponent(@Nullable String text);
}
