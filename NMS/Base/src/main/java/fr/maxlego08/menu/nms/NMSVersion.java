package fr.maxlego08.menu.nms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NMSVersion {
    /**
     * The minimum Minecraft version required for this implementation.
     * Format: "major.minor.patch" (e.g., "1.21", "1.20.5")
     */
    String value();
}
