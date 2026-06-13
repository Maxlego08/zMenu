package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import org.bukkit.Registry;
import org.bukkit.entity.Cat;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class CatVariantLoader extends RegistryVariantLoader<Cat.Type> {
    public CatVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("cat/variant", Registry.CAT_VARIANT, variantFactory::createCatVariant);
    }
}
