package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import org.jetbrains.annotations.NotNull;

/**
 * A named animation, loaded from the {@code animations} folder or registered by a plugin.
 *
 * <p>An inventory uses one with {@code open-animation: {file: name}}, and anything it writes next to
 * {@code file} overrides the shared settings key by key.</p>
 *
 * @param name     the name the animation is known by, which is its file name without the extension
 * @param settings what the animation does
 */
public record Animation(@NotNull String name, @NotNull SlotAnimationSettings settings) {
}
