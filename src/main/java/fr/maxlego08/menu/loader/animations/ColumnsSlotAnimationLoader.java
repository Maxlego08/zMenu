package fr.maxlego08.menu.loader.animations;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.ColumnsSlotAnimation;
import fr.maxlego08.menu.api.annotations.AutoSlotAnimationLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The {@code columns} animation.
 */
@AutoSlotAnimationLoader
public class ColumnsSlotAnimationLoader extends SlotAnimationLoader {

    private final SlotAnimation animation = new ColumnsSlotAnimation();

    public ColumnsSlotAnimationLoader() {
        super(ZMenuKeys.key("columns"));
    }

    @Override
    public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
        return this.animation;
    }
}
