package fr.maxlego08.menu.loader.components.variants.chicken;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.chicken.ChickenSoundVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Chicken;

@AutoComponentLoader
@SinceVersion("26.1")
public final class ChickenSoundVariantLoader extends RegistryVariantLoader<Chicken.SoundVariant> {
    public ChickenSoundVariantLoader() {
        super("chicken/sound-variant", RegistryKey.CHICKEN_SOUND_VARIANT, ChickenSoundVariantComponent::new);
    }
}
