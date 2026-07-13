package fr.maxlego08.menu.loader.components.variants.cat;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.cat.CatVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Cat;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class CatVariantLoader extends RegistryVariantLoader<Cat.Type> {
    public CatVariantLoader() {
        super("cat/variant", RegistryKey.CAT_VARIANT, CatVariantComponent::new);
    }
}
