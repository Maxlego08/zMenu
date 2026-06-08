package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.loader.components.variants.base.DyeColorVariantLoader;
import fr.maxlego08.menu.common.interfaces.VariantComponent;

@ComponentLoader
@SinceVersion("1.20.5")
public class TropicalFishPatternColorVariantLoader extends DyeColorVariantLoader {
    public TropicalFishPatternColorVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("tropical_fish/pattern_color", variantFactory::createTropicalFishPatternColor);
    }
}
