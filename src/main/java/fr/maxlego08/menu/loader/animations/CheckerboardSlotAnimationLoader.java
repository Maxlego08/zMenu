package fr.maxlego08.menu.loader.animations;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.CheckerboardSlotAnimation;
import fr.maxlego08.menu.api.annotations.AutoSlotAnimationLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The {@code checkerboard} animation.
 */
@AutoSlotAnimationLoader
public class CheckerboardSlotAnimationLoader extends SlotAnimationLoader {

    private final SlotAnimation animation = new CheckerboardSlotAnimation();

    public CheckerboardSlotAnimationLoader() {
        super(ZMenuKeys.key("checkerboard"));
    }

    @Override
    public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
        return this.animation;
    }
}
