package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import fr.maxlego08.menu.test.common.interfaces.VariantComponent;
import org.bukkit.entity.Horse;

@ComponentLoader
@SinceVersion("1.20.5")
public class HorseVariantLoader extends EnumVariantLoader<Horse.Color> {
    public HorseVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("horse/variant", Horse.Color.class, variantFactory::createHorse);
    }
}
