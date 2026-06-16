package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Parrot;

@AutoComponentLoader
@SinceVersion("1.12")
public class ParrotVariantLoader extends EnumVariantLoader<Parrot.Variant> {
    public ParrotVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("parrot/variant", Parrot.Variant.class, variantFactory::createParrot);
    }
}
