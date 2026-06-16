package fr.maxlego08.menu.api.attribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Strategy for merging attribute modifiers when applying custom attributes to items.
 * Defines how to handle conflicts between existing item attributes and new custom attributes.
 */
public enum AttributeMergeStrategy {

    /**
     * Replace all existing attribute modifiers with the new ones.
     * Any attributes not specified in the custom attributes will be removed.
     */
    REPLACE,

    /**
     * Add new attribute modifiers while keeping all existing ones.
     * This may result in duplicate modifiers for the same attribute.
     */
    ADD,

    /**
     * For each attribute, keep the modifier with the highest value.
     * If multiple modifiers exist for the same attribute, only the one with max value is kept.
     */
    KEEP_HIGHEST,

    /**
     * For each attribute, keep the modifier with the lowest value.
     * If multiple modifiers exist for the same attribute, only the one with min value is kept.
     */
    KEEP_LOWEST,

    /**
     * For each attribute, sum all modifiers with the same operation.
     * Combines the amounts of modifiers that share the same attribute and operation.
     */
    SUM;

    /**
     * Annotation to specify the default value for AttributeMergeStrategy fields.
     */
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.RECORD_COMPONENT})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DefaultStrategy {

        /**
         * The default AttributeMergeStrategy value.
         * @return the default strategy
         **/
        AttributeMergeStrategy value();
    }

}