package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.animation.slot.settings.ButtonAnimationSettings;
import fr.maxlego08.menu.api.button.buttons.ItemDragButton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the class level refusal of temporary items.
 */
class ButtonTemporaryItemsTest {

    @Test
    void aButtonRefusesToBeCoveredByDefault() {
        Button button = new PlainButton();

        assertFalse(button.allowsTemporaryItems());
        assertFalse(button.getAnimationSettings().allowTemporaryItems());
    }

    @Test
    void aDragButtonOptsIn() {
        Button button = new ItemDragButton() {
        };

        assertTrue(button.allowsTemporaryItems());
        assertTrue(button.getAnimationSettings().allowTemporaryItems());
    }

    @Test
    void aButtonBuiltInCodeFollowsItsOwnClass() {
        assertFalse(new PlainButton().getAnimationSettings().allowTemporaryItems());
        assertTrue(new CoverableButton().getAnimationSettings().allowTemporaryItems());
    }

    @Test
    void theConfigurationMayAllowAButtonThatWouldHaveRefused() {
        Button button = new PlainButton();
        button.setAnimationSettings(new ButtonAnimationSettings(true, 1, 0, null, true));

        assertTrue(button.getAnimationSettings().allowTemporaryItems());
    }

    @Test
    void theConfigurationMayRefuseAButtonThatWouldHaveAllowed() {
        Button button = new CoverableButton();
        button.setAnimationSettings(new ButtonAnimationSettings(true, 1, 0, null, false));

        assertTrue(button.allowsTemporaryItems());
        assertFalse(button.getAnimationSettings().allowTemporaryItems());
    }

    @Test
    void theOtherAnimationSettingsAreUntouched() {
        Button button = new PlainButton();
        button.setAnimationSettings(new ButtonAnimationSettings(true, 3, 250, null, true));

        ButtonAnimationSettings settings = button.getAnimationSettings();
        assertTrue(settings.allowTemporaryItems());
        assertTrue(settings.enabled());
        assertEquals(3, settings.group());
        assertEquals(250, settings.delay());
    }

    private static final class PlainButton extends Button {
    }

    /**
     * A button that is safe to draw over, and says so.
     */
    private static final class CoverableButton extends Button {
        @Override
        public boolean allowsTemporaryItems() {
            return true;
        }
    }
}
