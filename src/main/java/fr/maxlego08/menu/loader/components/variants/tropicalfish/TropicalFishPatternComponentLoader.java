package fr.maxlego08.menu.loader.components.variants.tropicalfish;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.tropicalfish.TropicalFishPatternComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.TropicalFish;

@AutoComponentLoader
@SinceVersion("1.20.5")
public final class TropicalFishPatternComponentLoader extends EnumVariantLoader<TropicalFish.Pattern> {

    public TropicalFishPatternComponentLoader() {
        super("tropical-fish/pattern", TropicalFish.Pattern.class, TropicalFishPatternComponent::new);
    }
}
