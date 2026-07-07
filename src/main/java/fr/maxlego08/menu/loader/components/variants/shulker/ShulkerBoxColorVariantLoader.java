package fr.maxlego08.menu.loader.components.variants.shulker;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.shulker.ShulkerColorComponent;
import fr.maxlego08.menu.loader.components.variants.base.DyeColorLoader;

@AutoComponentLoader
@SinceVersion("1.11")
public class ShulkerBoxColorVariantLoader extends DyeColorLoader {
    public ShulkerBoxColorVariantLoader() {
        super("shulker/color", ShulkerColorComponent::new);
    }
}
