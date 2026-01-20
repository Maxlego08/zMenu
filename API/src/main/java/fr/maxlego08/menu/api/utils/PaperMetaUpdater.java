package fr.maxlego08.menu.api.utils;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PaperMetaUpdater extends MetaUpdater {
    @NotNull Component getComponent(@Nullable String text);
}
