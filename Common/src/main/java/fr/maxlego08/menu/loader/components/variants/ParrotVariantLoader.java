package fr.maxlego08.menu.loader.components.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Parrot;

@ComponentLoader
@SinceVersion("1.12")
public class ParrotVariantLoader extends EnumVariantLoader<Parrot.Variant> {
    public ParrotVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("parrot/variant", Parrot.Variant.class, variantFactory::createParrot);
    }
}
