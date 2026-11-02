package fr.maxlego08.menu.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers a {@link fr.maxlego08.menu.api.dialog.canvas.CanvasPackTarget} of zMenu automatically when the
 * canvas manager starts. The class needs a constructor taking a {@link fr.maxlego08.menu.api.MenuPlugin},
 * or none, and is skipped when its {@link RequiresPlugin} is missing. A target that is also a
 * {@link org.bukkit.event.Listener} is registered as one.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoCanvasPackTarget {
}
