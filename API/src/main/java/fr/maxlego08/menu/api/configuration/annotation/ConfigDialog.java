package fr.maxlego08.menu.api.configuration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigDialog {
    String name() default "";
    String externalTitle() default "";
    String yesText() default "<green>Confirm";
    String noText() default "<red>Cancel";
    int yesWidth() default 150;
    int noWidth() default 150;
    String booleanConfirmText() default "<white>%key% :</white> %value% <gray>|</gray> %text%";
    String numberRangeConfirmText() default "<white>%key% :</white> %value%";
    String textConfirmText() default "<white>%key% : <gray>%text%";
}
