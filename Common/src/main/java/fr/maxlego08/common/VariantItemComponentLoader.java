package fr.maxlego08.common;

public abstract class VariantItemComponentLoader {
    private final VariantComponent variant;

    public VariantItemComponentLoader(VariantComponent variant) {
        this.variant = variant;
    }

    public VariantComponent getVariant() {
        return variant;
    }

}
