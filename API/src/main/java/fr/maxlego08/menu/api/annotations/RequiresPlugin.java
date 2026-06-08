package fr.maxlego08.menu.api.annotations;

import fr.maxlego08.menu.api.utils.Comparison;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequiresPlugin {
    @NotNull
    String value();

    @NotNull
    String version() default "";

    @NotNull
    Comparison type() default Comparison.GREATER_THAN_OR_EQUAL_TO;

    @NotNull
    CheckMode checkMode() default CheckMode.EXISTS;

    enum CheckMode {
        EXISTS,
        EXISTS_AND_ENABLED
    }

}
