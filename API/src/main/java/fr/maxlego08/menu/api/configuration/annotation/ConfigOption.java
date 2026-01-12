package fr.maxlego08.menu.api.configuration.annotation;

import fr.maxlego08.menu.api.enums.DialogInputType;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigOption {
    @NotNull
    String key() default "";
    @NotNull
    DialogInputType type() default DialogInputType.TEXT;
    @NotNull
    String label() default "";
    int width() default 200;
    boolean labelVisible() default true;

    // For text type
    int maxLength() default 32;
    int multilineMaxLines() default 0;
    int multilineHeight() default 0;

    // For boolean type
    @NotNull
    String trueText() default "<green>True";
    @NotNull
    String falseText() default "<red>False";

    // For number range type
    float startRange() default 0;
    float endRange() default 100;
    float stepRange() default 1;

}