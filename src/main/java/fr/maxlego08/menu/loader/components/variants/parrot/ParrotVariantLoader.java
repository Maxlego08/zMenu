package fr.maxlego08.menu.loader.components.variants.parrot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.parrot.ParrotVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Parrot;

@AutoComponentLoader
@SinceVersion("1.12")
public class ParrotVariantLoader extends EnumVariantLoader<Parrot.Variant> {
    public ParrotVariantLoader() {
        super("parrot/variant", Parrot.Variant.class, ParrotVariantComponent::new);
    }
}
