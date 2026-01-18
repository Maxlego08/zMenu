package fr.maxlego08.common.loader.components;

import fr.maxlego08.common.interfaces.VariantComponent;

public abstract class VariantItemComponentLoader {
    private final VariantComponent variant;

    public VariantItemComponentLoader(VariantComponent variant) {
        this.variant = variant;
    }

    public VariantComponent getVariant() {
        return variant;
    }

}
