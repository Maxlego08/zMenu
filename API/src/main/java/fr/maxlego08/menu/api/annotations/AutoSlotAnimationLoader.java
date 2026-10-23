package fr.maxlego08.menu.api.annotations;

import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@link SlotAnimationLoader} of zMenu that is
 * registered at startup.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoSlotAnimationLoader {
}
