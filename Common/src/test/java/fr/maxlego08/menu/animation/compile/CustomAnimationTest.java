package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.animation.slot.*;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.SpiralSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.WaveSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the animation written in code rather than in a file.
 */
class CustomAnimationTest {

    private static final MenuItemStack ITEM = TestMenuItemStacks.named("flash");

    @Test
    void anAnimationOverridingNothingIsStillUsable() {
        Animation animation = new MinimalAnimation().toAnimation();

        assertEquals(ZMenuKeys.key("minimal"), animation.key());
        assertEquals("zmenu:minimal", animation.name());

        SlotAnimationGroup defaults = animation.settings().defaults();
        assertInstanceOf(SequentialSlotAnimation.class, defaults.animation());
        assertEquals(50, defaults.interval());
        assertEquals(1, defaults.slotsPerStep());
        assertEquals(0, defaults.delay());
        assertFalse(defaults.reverse());
        assertTrue(animation.settings().freeze());
        assertEquals(AnimationPlayMode.ALWAYS, animation.settings().play());
        assertTrue(animation.settings().temporaryItems().isEmpty());
    }

    @Test
    void everyOverrideReachesTheSettings() {
        SlotAnimationSettings settings = new FullAnimation().toSettings();
        SlotAnimationGroup defaults = settings.defaults();

        assertInstanceOf(SpiralSlotAnimation.class, defaults.animation());
        assertEquals(25, defaults.interval());
        assertEquals(3, defaults.slotsPerStep());
        assertEquals(120, defaults.delay());
        assertTrue(defaults.reverse());
        assertSame(TestSounds.SILENT, defaults.sound());
        assertFalse(settings.freeze());
        assertEquals(AnimationPlayMode.ONCE, settings.play());
    }

    @Test
    void groupsAreNumberedFromTheFirstOneGiven() {
        SlotAnimationSettings settings = new GroupedAnimation().toSettings();

        assertEquals(2, settings.groups().size());
        assertEquals(60, settings.group(2).interval());
        assertEquals(10, settings.group(3).interval());
        assertEquals(50, settings.group(7).interval());
    }

    @Test
    void aGroupInheritsTheSoundAndDirectionOfTheClass() {
        SlotAnimationGroup group = new GroupedAnimation().toSettings().group(2);

        assertSame(TestSounds.SILENT, group.sound());
        assertTrue(group.reverse());
        assertInstanceOf(WaveSlotAnimation.class, group.animation());
    }

    @Test
    void theTemporaryItemHelpersBuildTheSameThingAsTheFileWould() {
        SlotAnimationSettings settings = new TemporaryAnimation().toSettings();

        assertEquals(2, settings.temporaryItems().size());

        TemporaryItem simple = settings.temporaryItems().getFirst();
        assertIterableEquals(List.of(10, 11, 12), simple.slots());
        assertEquals(2, simple.frames().size());
        assertSame(ITEM, simple.frames().getFirst().item());
        assertTrue(simple.frames().get(1).isRemoval());

        TemporaryItem keyframed = settings.temporaryItems().get(1);
        assertEquals(3, keyframed.frames().size());
        assertTrue(keyframed.frames().get(2).isRemoval());
    }

    @Test
    void aCustomAnimationCompilesLikeAnyOther() {
        SlotAnimationSettings settings = new GroupedAnimation().toSettings();

        CompiledAnimation compiled = AnimationCompiler.compile(27, settings, List.of(0, 1, 2), Map.of());
        assertFalse(compiled.isEmpty());
        assertEquals(2, compiled.steps().getFirst().slot());
    }

    private static final class MinimalAnimation extends CustomAnimation {
        private MinimalAnimation() {
            super(ZMenuKeys.key("minimal"));
        }
    }

    private static final class FullAnimation extends CustomAnimation {
        private FullAnimation() {
            super(ZMenuKeys.key("full"));
        }

        @Override
        public @NonNull SlotAnimation getAnimation() {
            return new SpiralSlotAnimation();
        }

        @Override
        public long getInterval() {
            return 25;
        }

        @Override
        public int getSlotsPerStep() {
            return 3;
        }

        @Override
        public long getDelay() {
            return 120;
        }

        @Override
        public boolean isReverse() {
            return true;
        }

        @Override
        public SoundOption getSound() {
            return TestSounds.SILENT;
        }

        @Override
        public boolean isFreeze() {
            return false;
        }

        @Override
        public @NonNull AnimationPlayMode getPlayMode() {
            return AnimationPlayMode.ONCE;
        }
    }

    private static final class GroupedAnimation extends CustomAnimation {
        private GroupedAnimation() {
            super(ZMenuKeys.key("grouped"));
        }

        @Override
        public boolean isReverse() {
            return true;
        }

        @Override
        public SoundOption getSound() {
            return TestSounds.SILENT;
        }

        @Override
        public @NonNull Map<Integer, SlotAnimationGroup> getGroups() {
            return this.groups(2,
                    this.group(new WaveSlotAnimation(), 60, 1, 0),
                    this.group(null, 10, 1, 0));
        }
    }

    private static final class TemporaryAnimation extends CustomAnimation {
        private TemporaryAnimation() {
            super(ZMenuKeys.key("temporary"));
        }

        @Override
        public @NonNull List<TemporaryItem> getTemporaryItems() {
            return List.of(
                    this.temporaryItem("simple", this.slots(10, 12), ITEM, AnimationAnchor.START, AnimationAnchor.at(300)),
                    this.temporaryItem("keyframed", this.slots(20, 20),
                            this.frame(AnimationAnchor.START, ITEM, null),
                            this.frame(AnimationAnchor.at(200), ITEM, TestSounds.SILENT),
                            this.removal(AnimationAnchor.at(400), null)));
        }
    }
}
