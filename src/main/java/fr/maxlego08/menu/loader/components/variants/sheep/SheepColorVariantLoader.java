package fr.maxlego08.menu.loader.components.variants.sheep;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.sheep.SheepColorComponent;
import fr.maxlego08.menu.loader.components.variants.base.DyeColorLoader;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SheepColorVariantLoader extends DyeColorLoader {
    public SheepColorVariantLoader() {
        super("sheep/color", SheepColorComponent::new);
    }
}
