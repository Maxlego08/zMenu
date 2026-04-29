package fr.maxlego08.menu.loader.components.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.DyeColorVariantLoader;

@ComponentLoader
@SinceVersion("1.11")
public class ShulkerBoxColorVariantLoader extends DyeColorVariantLoader {
    public ShulkerBoxColorVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("shulker/color", variantFactory::createShulkerBox);
    }
}
