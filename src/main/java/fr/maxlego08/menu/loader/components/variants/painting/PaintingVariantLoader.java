package fr.maxlego08.menu.loader.components.variants.painting;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.painting.PaintingVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Art;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class PaintingVariantLoader extends RegistryVariantLoader<Art> {
    public PaintingVariantLoader() {
        super("painting/variant", RegistryKey.PAINTING_VARIANT, PaintingVariantComponent::new);
    }
}
