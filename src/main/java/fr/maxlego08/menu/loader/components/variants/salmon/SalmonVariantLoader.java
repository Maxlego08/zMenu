package fr.maxlego08.menu.loader.components.variants.salmon;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.salmon.SalmonSizeComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Salmon;

@AutoComponentLoader
@SinceVersion("1.21.5")
public class SalmonVariantLoader extends EnumVariantLoader<Salmon.Variant> {
    public SalmonVariantLoader() {
        super("salmon/size", Salmon.Variant.class, SalmonSizeComponent::new);
    }
}
