package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import org.bukkit.Registry;
import org.bukkit.entity.Wolf;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class WolfVariantLoader extends RegistryVariantLoader<Wolf.Variant> {
    public WolfVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("wolf/variant", Registry.WOLF_VARIANT, variantFactory::createWolfVariant);
    }
}
