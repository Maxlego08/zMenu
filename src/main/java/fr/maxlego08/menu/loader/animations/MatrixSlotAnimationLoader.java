package fr.maxlego08.menu.loader.animations;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.MatrixSlotAnimation;
import fr.maxlego08.menu.api.annotations.AutoSlotAnimationLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The {@code matrix} animation, drawn by the {@code matrix} option.
 */
@AutoSlotAnimationLoader
public class MatrixSlotAnimationLoader extends SlotAnimationLoader {

    public MatrixSlotAnimationLoader() {
        super(ZMenuKeys.key("matrix"));
    }

    @Override
    public @NotNull SlotAnimation create(@NotNull Map<String, Object> options) {
        Object value = options.get("matrix");
        if (!(value instanceof List<?> list) || list.isEmpty()) {
            throw new IllegalArgumentException("the MATRIX animation needs a matrix, a list of lines of at most 9 characters");
        }

        List<String> rows = new ArrayList<>(list.size());
        for (Object line : list) {
            rows.add(String.valueOf(line));
        }
        return new MatrixSlotAnimation(rows);
    }
}
