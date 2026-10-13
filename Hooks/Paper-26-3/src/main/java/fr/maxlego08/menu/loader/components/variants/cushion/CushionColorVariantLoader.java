package fr.maxlego08.menu.loader.components.variants.cushion;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.cushion.CushionColorComponent;
import fr.maxlego08.menu.loader.components.variants.base.DyeColorLoader;

@AutoComponentLoader
@SinceVersion("26.3")
public class CushionColorVariantLoader extends DyeColorLoader {
    public CushionColorVariantLoader() {
        super("cushion/color", CushionColorComponent::new);
    }
}
