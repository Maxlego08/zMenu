package fr.maxlego08.menu.loader.components.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.CollarColorLoader;

@ComponentLoader
@SinceVersion("1.20.5")
public class CatCollarVariantLoader extends CollarColorLoader {
    public CatCollarVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("cat/collar", variantFactory::createCatCollar);
    }
}
