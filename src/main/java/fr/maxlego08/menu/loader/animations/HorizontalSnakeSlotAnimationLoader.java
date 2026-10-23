package fr.maxlego08.menu.loader.animations;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.HorizontalSnakeSlotAnimation;
import fr.maxlego08.menu.api.annotations.AutoSlotAnimationLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * The {@code horizontal_snake} animation.
 */
@AutoSlotAnimationLoader
public class HorizontalSnakeSlotAnimationLoader extends SlotAnimationLoader {

    private final SlotAnimation animation = new HorizontalSnakeSlotAnimation();

    public HorizontalSnakeSlotAnimationLoader() {
        super(ZMenuKeys.key("horizontal_snake"));
    }

    @Override
    public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
        return this.animation;
    }
}
