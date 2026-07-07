package fr.maxlego08.menu.loader.components.variants.pig;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.pig.PigVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import org.bukkit.entity.Pig;

@AutoComponentLoader
@PaperOnly
@SinceVersion("1.21.5")
public final class PigVariantLoader extends RegistryVariantLoader<Pig.Variant> {
    public PigVariantLoader() {
        super("pig/variant", Pig.Variant.class, PigVariantComponent::new);
    }
}
