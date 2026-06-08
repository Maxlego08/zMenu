package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import fr.maxlego08.menu.test.common.interfaces.VariantComponent;
import org.bukkit.Registry;
import org.bukkit.entity.Cat;

@ComponentLoader
@SinceVersion("1.20.5")
public class CatVariantLoader extends RegistryVariantLoader<Cat.Type> {
    public CatVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("cat/variant", Registry.CAT_VARIANT, variantFactory::createCatVariant);
    }
}
