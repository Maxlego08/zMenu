package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.loader.components.variants.base.CollarColorLoader;
import fr.maxlego08.menu.test.common.interfaces.VariantComponent;

@ComponentLoader
@SinceVersion("1.20.5")
public class WolfCollarVariantLoader extends CollarColorLoader {
    public WolfCollarVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("wolf/collar", variantFactory::createWolfCollar);
    }
}
