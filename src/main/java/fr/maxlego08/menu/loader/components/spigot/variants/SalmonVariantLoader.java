package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Salmon;

@AutoComponentLoader
@SinceVersion("1.21.5")
public class SalmonVariantLoader extends EnumVariantLoader<Salmon.Variant> {
    public SalmonVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("salmon/size", Salmon.Variant.class, variantFactory::createSalmon);
    }
}
