package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.annotations.SpigotOnly;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import fr.maxlego08.menu.test.common.interfaces.VariantComponent;
import org.bukkit.Registry;
import org.bukkit.entity.Cow;

@ComponentLoader
@SpigotOnly
@SinceVersion("1.21.5")
public class SpigotCowVariantLoader extends RegistryVariantLoader<Cow.Variant> {
    public SpigotCowVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("cow/variant", Registry.COW_VARIANT, variantFactory::createCow);
    }
}
