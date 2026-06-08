package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import fr.maxlego08.menu.test.common.interfaces.VariantComponent;
import org.bukkit.Registry;
import org.bukkit.entity.Wolf;

@ComponentLoader
@SinceVersion("1.20.5")
public class WolfVariantLoader extends RegistryVariantLoader<Wolf.Variant> {
    public WolfVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("wolf/variant", Registry.WOLF_VARIANT, variantFactory::createWolfVariant);
    }
}
