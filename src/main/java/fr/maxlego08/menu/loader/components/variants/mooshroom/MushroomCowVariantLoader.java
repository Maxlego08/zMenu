package fr.maxlego08.menu.loader.components.variants.mooshroom;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.mooshroom.MushroomVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.MushroomCow;

@AutoComponentLoader
@SinceVersion("1.13")
public class MushroomCowVariantLoader extends EnumVariantLoader<MushroomCow.Variant> {
    public MushroomCowVariantLoader() {
        super("mooshroom/variant", MushroomCow.Variant.class, MushroomVariantComponent::new);
    }
}
