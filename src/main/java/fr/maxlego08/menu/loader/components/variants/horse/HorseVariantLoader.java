package fr.maxlego08.menu.loader.components.variants.horse;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.horse.HorseVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Horse;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class HorseVariantLoader extends EnumVariantLoader<Horse.Color> {
    public HorseVariantLoader() {
        super("horse/variant", Horse.Color.class, HorseVariantComponent::new);
    }
}
