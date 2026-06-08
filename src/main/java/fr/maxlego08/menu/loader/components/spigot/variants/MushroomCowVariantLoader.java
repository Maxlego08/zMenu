package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.MushroomCow;

@AutoComponentLoader
@SinceVersion("1.13")
public class MushroomCowVariantLoader extends EnumVariantLoader<MushroomCow.Variant> {
    public MushroomCowVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("mooshroom/variant", MushroomCow.Variant.class, variantFactory::createMushroomCow);
    }
}
