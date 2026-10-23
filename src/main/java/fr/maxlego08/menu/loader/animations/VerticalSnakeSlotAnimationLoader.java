package fr.maxlego08.menu.loader.animations;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.VerticalSnakeSlotAnimation;
import fr.maxlego08.menu.api.annotations.AutoSlotAnimationLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The {@code vertical_snake} animation.
 */
@AutoSlotAnimationLoader
public class VerticalSnakeSlotAnimationLoader extends SlotAnimationLoader {

    private final SlotAnimation animation = new VerticalSnakeSlotAnimation();

    public VerticalSnakeSlotAnimationLoader() {
        super(ZMenuKeys.key("vertical_snake"));
    }

    @Override
    public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
        return this.animation;
    }
}
