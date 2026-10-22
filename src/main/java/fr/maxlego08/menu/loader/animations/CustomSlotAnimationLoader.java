package fr.maxlego08.menu.loader.animations;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.CustomSlotAnimation;
import fr.maxlego08.menu.api.annotations.AutoSlotAnimationLoader;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The {@code custom} animation, revealing the slots in the {@code order} option.
 */
@AutoSlotAnimationLoader
public class CustomSlotAnimationLoader extends SlotAnimationLoader {

    public CustomSlotAnimationLoader() {
        super(ZMenuKeys.key("custom"));
    }

    @Override
    public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
        Object value = options.get("order");
        if (!(value instanceof List<?> list) || list.isEmpty()) {
            throw new IllegalArgumentException("the CUSTOM animation needs an order, a list of slots such as 13 or 4-8");
        }

        List<String> lines = new ArrayList<>(list.size());
        for (Object line : list) {
            lines.add(String.valueOf(line));
        }

        List<Integer> order = ButtonLoader.loadSlot(lines);
        if (order.isEmpty()) {
            throw new IllegalArgumentException("the order of the CUSTOM animation holds no valid slot");
        }
        return new CustomSlotAnimation(order);
    }
}
