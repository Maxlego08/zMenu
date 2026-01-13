package fr.maxlego08.menu.api.animation;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public record TitleAnimationSettings(
    List<@NotNull String> titles,
    int cycles,
    int initialDelay,
    int interval,
    @NotNull TimeUnit timeUnit,
    boolean showItemsAfterAnimation,
    int itemUpdateInterval
) {
}
