package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.CollarColorLoader;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class WolfCollarVariantLoader extends CollarColorLoader {
    public WolfCollarVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("wolf/collar", variantFactory::createWolfCollar);
    }
}
