package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.CustomAnimation;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationSeed;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.requirement.Action;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the actions an animation runs when it starts and when it ends. Whether they run is
 * the engine's business; what is tested here is that they are carried and described.
 */
class AnimationActionsTest {

    @Test
    void anAnimationHasNoActionsByDefault() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0), true);

        assertTrue(settings.startActions().isEmpty());
        assertTrue(settings.endActions().isEmpty());
    }

    @Test
    void theActionsAreCarriedByTheSettings() {
        Action start = new TestAction();
        Action end = new TestAction();

        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0),
                Map.of(), true, AnimationPlayMode.ALWAYS, List.of(), null, 100, List.of(),
                AnimationSeed.RANDOM, List.of(start), List.of(end));

        assertIterableEquals(List.of(start), settings.startActions());
        assertIterableEquals(List.of(end), settings.endActions());
    }

    @Test
    void anAnimationWrittenInCodeMayCarryThemToo() {
        SlotAnimationSettings settings = new ActingAnimation().toSettings();

        assertIterableEquals(List.of(ActingAnimation.START), settings.startActions());
        assertIterableEquals(List.of(ActingAnimation.END), settings.endActions());
    }

    @Test
    void anAnimationWrittenInCodeHasNoneByDefault() {
        SlotAnimationSettings settings = new PlainAnimation().toSettings();

        assertTrue(settings.startActions().isEmpty());
        assertTrue(settings.endActions().isEmpty());
    }

    private static final class TestAction extends Action {
        @Override
        protected void execute(org.bukkit.entity.Player player, fr.maxlego08.menu.api.button.Button button,
                               fr.maxlego08.menu.api.engine.InventoryEngine inventory,
                               fr.maxlego08.menu.api.utils.Placeholders placeholders) {
        }
    }

    private static final class ActingAnimation extends CustomAnimation {
        private static final Action START = new TestAction();
        private static final Action END = new TestAction();

        private ActingAnimation() {
            super(ZMenuKeys.key("acting"));
        }

        @Override
        public List<Action> getStartActions() {
            return List.of(START);
        }

        @Override
        public @NonNull List<Action> getEndActions() {
            return List.of(END);
        }
    }

    private static final class PlainAnimation extends CustomAnimation {
        private PlainAnimation() {
            super(ZMenuKeys.key("plain"));
        }
    }
}
