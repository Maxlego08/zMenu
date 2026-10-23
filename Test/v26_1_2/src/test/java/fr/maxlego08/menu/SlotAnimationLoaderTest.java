package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.loader.animations.CustomSlotAnimationLoader;
import fr.maxlego08.menu.loader.animations.MatrixSlotAnimationLoader;
import fr.maxlego08.menu.loader.animations.SequentialSlotAnimationLoader;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The loaders that read the options of an animation group.
 */
public class SlotAnimationLoaderTest {

    private static final int SIZE = 90;

    @Test
    public void theMatrixLoaderReadsTheMatrixOption() {
        SlotAnimation animation = new MatrixSlotAnimationLoader().create(Map.of("matrix", List.of("21", "12")));

        assertIterableEquals(List.of(1, 9, 0, 10), animation.order(SIZE, List.of(0, 1, 9, 10)));
    }

    @Test
    public void theMatrixLoaderExplainsItselfWhenTheMatrixIsMissing() {
        MatrixSlotAnimationLoader loader = new MatrixSlotAnimationLoader();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loader.create(Map.of()));

        assertEquals("the MATRIX animation needs a matrix, a list of lines of at most 9 characters", exception.getMessage());
        assertThrows(IllegalArgumentException.class, () -> loader.create(Map.of("matrix", "111")));
        assertThrows(IllegalArgumentException.class, () -> loader.create(Map.of("matrix", List.of())));
    }

    @Test
    public void theCustomLoaderAcceptsSingleSlotsAndRanges() {
        SlotAnimation animation = new CustomSlotAnimationLoader().create(Map.of("order", List.of(13, "10-12")));

        assertIterableEquals(List.of(13, 10, 11, 12), animation.order(SIZE, List.of(10, 11, 12, 13)));
    }

    @Test
    public void theCustomLoaderExplainsItselfWhenTheOrderIsMissingOrEmpty() {
        CustomSlotAnimationLoader loader = new CustomSlotAnimationLoader();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> loader.create(Map.of()));
        assertEquals("the CUSTOM animation needs an order, a list of slots such as 13 or 4-8", exception.getMessage());

        assertThrows(IllegalArgumentException.class, () -> loader.create(Map.of("order", List.of())));
        assertThrows(IllegalArgumentException.class, () -> loader.create(Map.of("order", "13")));
        assertThrows(IllegalArgumentException.class, () -> loader.create(Map.of("order", List.of("abc"))));
    }

    @Test
    public void anAnimationWithoutOptionsAlwaysReturnsTheSameInstanceAndKnowsItsAlias() {
        SequentialSlotAnimationLoader loader = new SequentialSlotAnimationLoader();

        assertSame(loader.create(Map.of()), loader.create(Map.of("matrix", List.of("1"))));
        assertEquals(ZMenuKeys.key("sequential"), loader.key());
        assertEquals(List.of(ZMenuKeys.key("rows")), loader.aliases());
    }
}
