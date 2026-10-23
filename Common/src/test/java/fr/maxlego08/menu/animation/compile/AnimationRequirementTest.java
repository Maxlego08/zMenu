package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.CustomAnimation;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.requirement.Requirement;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for the requirement carried by an animation. Whether it passes is decided by the
 * engine, which has a player; what is tested here is that it survives being built and described.
 */
class AnimationRequirementTest {

    private static final Requirement REQUIREMENT = (Requirement) Proxy.newProxyInstance(
            AnimationRequirementTest.class.getClassLoader(),
            new Class<?>[]{Requirement.class},
            (proxy, method, args) -> switch (method.getName()) {
                case "toString" -> "requirement";
                case "hashCode" -> 0;
                case "equals" -> proxy == args[0];
                default -> null;
            });

    @Test
    void anAnimationHasNoRequirementByDefault() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0), true);

        assertNull(settings.requirement());
    }

    @Test
    void theRequirementIsCarriedByTheSettings() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0),
                Map.of(), true, AnimationPlayMode.ALWAYS, List.of(), REQUIREMENT);

        assertSame(REQUIREMENT, settings.requirement());
    }

    @Test
    void anAnimationWrittenInCodeMayGateItself() {
        SlotAnimationSettings settings = new GatedAnimation().toSettings();

        assertNotNull(settings.requirement());
        assertSame(REQUIREMENT, settings.requirement());
    }

    @Test
    void anAnimationWrittenInCodeHasNoRequirementByDefault() {
        assertNull(new PlainAnimation().toSettings().requirement());
    }

    private static final class GatedAnimation extends CustomAnimation {
        private GatedAnimation() {
            super(ZMenuKeys.key("gated"));
        }

        @Override
        public Requirement getRequirement() {
            return REQUIREMENT;
        }
    }

    private static final class PlainAnimation extends CustomAnimation {
        private PlainAnimation() {
            super(ZMenuKeys.key("plain"));
        }
    }
}
