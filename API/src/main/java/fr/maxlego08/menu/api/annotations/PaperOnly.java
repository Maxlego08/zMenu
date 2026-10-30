package fr.maxlego08.menu.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class that must only be loaded on Paper.
 *
 * @deprecated zMenu only runs on Paper and its forks, so every class already is.
 */
@Deprecated(since = "1.1.2.1", forRemoval = true)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PaperOnly {
}
