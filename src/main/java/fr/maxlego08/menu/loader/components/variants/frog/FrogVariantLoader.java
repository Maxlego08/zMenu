package fr.maxlego08.menu.loader.components.variants.frog;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.frog.FrogVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import org.bukkit.entity.Frog;

@AutoComponentLoader
@SinceVersion("1.18")
public class FrogVariantLoader extends RegistryVariantLoader<Frog.Variant> {
    public FrogVariantLoader() {
        super("frog/variant", Frog.Variant.class, FrogVariantComponent::new);
    }
}
