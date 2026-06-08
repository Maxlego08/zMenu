package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import org.bukkit.Registry;
import org.bukkit.entity.Frog;

@ComponentLoader
@SinceVersion("1.18")
public class FrogVariantLoader extends RegistryVariantLoader<Frog.Variant> {
    public FrogVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("frog/variant", Registry.FROG_VARIANT, variantFactory::createFrog);
    }
}
