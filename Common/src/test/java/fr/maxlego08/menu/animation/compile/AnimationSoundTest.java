package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.TestMenuItemStacks;
import fr.maxlego08.menu.api.animation.slot.TestSounds;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import fr.maxlego08.menu.api.animation.slot.settings.ButtonAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.item.AnimationKeyframe;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for the sounds carried by the animation, and for the rule keeping at most one of them
 * per instant.
 */
class AnimationSoundTest {

    private static final int SIZE = 27;
    private static final SoundOption GROUP_SOUND = TestSounds.SILENT;
    private static final SoundOption BUTTON_SOUND = TestSounds.other();

    @Test
    void aGroupSoundRidesOnEveryRevealItMakes() {
        List<AnimationStep> steps = compile(group(50, 1, GROUP_SOUND), List.of(0, 1, 2), Map.of());

        assertEquals(3, steps.size());
        steps.forEach(step -> assertSame(GROUP_SOUND, step.sound()));
    }

    @Test
    void aButtonSoundOverridesTheGroupOne() {
        List<AnimationStep> steps = compile(group(50, 1, GROUP_SOUND), List.of(0, 1),
                Map.of(1, new ButtonAnimationSettings(true, 1, 0, BUTTON_SOUND)));

        assertSame(GROUP_SOUND, stepOn(steps, 0).sound());
        assertSame(BUTTON_SOUND, stepOn(steps, 1).sound());
    }

    @Test
    void onlyOneSoundIsPlayedPerInstant() {
        List<AnimationStep> steps = compile(group(50, 3, GROUP_SOUND), List.of(0, 1, 2), Map.of());

        assertEquals(1, steps.stream().filter(step -> step.sound() != null).count());
        assertSame(GROUP_SOUND, steps.getFirst().sound());
        assertNull(steps.get(1).sound());
        assertNull(steps.get(2).sound());
    }

    @Test
    void aKeyframeLandingOnARevealDoesNotDoubleUp() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                group(50, 1, GROUP_SOUND), Map.of(), true, AnimationPlayMode.ALWAYS,
                List.of(new TemporaryItem("test", List.of(20), List.of(
                        new AnimationKeyframe(AnimationAnchor.at(0), TestMenuItemStacks.named("x"), BUTTON_SOUND),
                        new AnimationKeyframe(AnimationAnchor.at(50), null, null)))));

        List<AnimationStep> steps = AnimationCompiler.compile(SIZE, settings, List.of(0, 1), Map.of()).steps();

        assertEquals(1, steps.stream().filter(step -> step.time() == 0 && step.sound() != null).count());
    }

    @Test
    void aSilentGroupStaysSilent() {
        List<AnimationStep> steps = compile(group(50, 1, null), List.of(0, 1), Map.of());

        steps.forEach(step -> assertNull(step.sound()));
    }

    private static SlotAnimationGroup group(long interval, int slotsPerStep, SoundOption sound) {
        return new SlotAnimationGroup(new SequentialSlotAnimation(), interval, slotsPerStep, 0, sound);
    }

    private static List<AnimationStep> compile(SlotAnimationGroup defaults, List<Integer> slots, Map<Integer, ButtonAnimationSettings> slotSettings) {
        return AnimationCompiler.compile(SIZE, new SlotAnimationSettings(defaults, true), slots, slotSettings).steps();
    }

    private static AnimationStep stepOn(List<AnimationStep> steps, int slot) {
        return steps.stream()
                .filter(step -> step.slot() == slot)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No step on slot " + slot));
    }
}
