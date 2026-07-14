package fr.maxlego08.menu.loader.components.variants.tropicalfish;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.tropicalfish.TropicalFishPatternColorComponent;
import fr.maxlego08.menu.loader.components.variants.base.DyeColorLoader;

@AutoComponentLoader
@SinceVersion("1.20.5")
public final class TropicalFishPatternColorComponentLoader extends DyeColorLoader {

    public TropicalFishPatternColorComponentLoader() {
        super("tropical-fish/pattern-color", TropicalFishPatternColorComponent::new);
    }
}
