package fr.maxlego08.menu.loader.components.variants.cow;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.cow.CowVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import org.bukkit.entity.Cow;

@AutoComponentLoader
@PaperOnly
@SinceVersion("1.21.5")
public final class CowVariantLoader extends RegistryVariantLoader<Cow.Variant> {
    public CowVariantLoader() {
        super("cow/variant", Cow.Variant.class, CowVariantComponent::new);
    }
}
