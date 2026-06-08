package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.annotations.SpigotOnly;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import org.bukkit.Registry;
import org.bukkit.entity.Chicken;

@AutoComponentLoader
@SpigotOnly
@SinceVersion("1.21.5")
public class SpigotChickenVariantLoader extends RegistryVariantLoader<Chicken.Variant> {
    public SpigotChickenVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("chicken/variant", Registry.CHICKEN_VARIANT, variantFactory::createChicken);
    }
}
