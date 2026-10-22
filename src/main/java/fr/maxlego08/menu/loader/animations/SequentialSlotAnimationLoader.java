package fr.maxlego08.menu.loader.animations;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.annotations.AutoSlotAnimationLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The {@code sequential} animation, also named {@code rows} since it walks the inventory row by row.
 */
@AutoSlotAnimationLoader
public class SequentialSlotAnimationLoader extends SlotAnimationLoader {

    private final SlotAnimation animation = new SequentialSlotAnimation();

    public SequentialSlotAnimationLoader() {
        super(ZMenuKeys.key("sequential"), ZMenuKeys.key("rows"));
    }

    @Override
    public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
        return this.animation;
    }
}
