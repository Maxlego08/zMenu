package fr.maxlego08.menu.api.annotations;

import fr.maxlego08.menu.api.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequireSupport {
    @NotNull SupportType value() default SupportType.DIALOG;

    enum SupportType {
        DIALOG {
            @Override
            public boolean evaluate() {
                return Configuration.HAS_DIALOG_SUPPORT;
            }
        },
        BEDROCK_INVENTORY {
            @Override
            public boolean evaluate() {
                return Configuration.HAS_BEDROCK_INVENTORY_SUPPORT;
            }
        };

        public abstract boolean evaluate();
    }
}
