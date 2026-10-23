package fr.maxlego08.menu.loader.animations;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.SpiralSlotAnimation;
import fr.maxlego08.menu.api.annotations.AutoSlotAnimationLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The {@code spiral} animation.
 */
@AutoSlotAnimationLoader
public class SpiralSlotAnimationLoader extends SlotAnimationLoader {

    private final SlotAnimation animation = new SpiralSlotAnimation();

    public SpiralSlotAnimationLoader() {
        super(ZMenuKeys.key("spiral"));
    }

    @Override
    public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
        return this.animation;
    }
}
