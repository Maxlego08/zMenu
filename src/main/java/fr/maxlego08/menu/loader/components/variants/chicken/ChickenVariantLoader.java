package fr.maxlego08.menu.loader.components.variants.chicken;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.chicken.ChickenVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Chicken;

@AutoComponentLoader
@PaperOnly
@SinceVersion("1.21.5")
public final class ChickenVariantLoader extends RegistryVariantLoader<Chicken.Variant> {

    public ChickenVariantLoader() {
        super("chicken/variant", RegistryKey.CHICKEN_VARIANT, ChickenVariantComponent::new);
    }

}
