package fr.maxlego08.menu.loader.components.variants.wolf;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.wolf.WolfCollarComponent;
import fr.maxlego08.menu.loader.components.variants.base.DyeColorLoader;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class WolfCollarVariantLoader extends DyeColorLoader {
    public WolfCollarVariantLoader() {
        super("wolf/collar", WolfCollarComponent::new);
    }
}
