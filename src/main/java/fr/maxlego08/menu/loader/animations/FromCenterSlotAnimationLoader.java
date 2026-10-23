package fr.maxlego08.menu.loader.animations;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.FromCenterSlotAnimation;
import fr.maxlego08.menu.api.annotations.AutoSlotAnimationLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The {@code from_center} animation.
 */
@AutoSlotAnimationLoader
public class FromCenterSlotAnimationLoader extends SlotAnimationLoader {

    private final SlotAnimation animation = new FromCenterSlotAnimation();

    public FromCenterSlotAnimationLoader() {
        super(ZMenuKeys.key("from_center"));
    }

    @Override
    public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
        return this.animation;
    }
}
