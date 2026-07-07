package fr.maxlego08.menu.loader.components.variants.cat;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.cat.CatCollarComponent;
import fr.maxlego08.menu.loader.components.variants.base.DyeColorLoader;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class CatCollarVariantLoader extends DyeColorLoader {
    public CatCollarVariantLoader() {
        super("cat/collar", CatCollarComponent::new);
    }
}
