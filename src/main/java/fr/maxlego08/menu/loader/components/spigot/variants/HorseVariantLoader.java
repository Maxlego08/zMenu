package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Horse;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class HorseVariantLoader extends EnumVariantLoader<Horse.Color> {
    public HorseVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("horse/variant", Horse.Color.class, variantFactory::createHorse);
    }
}
