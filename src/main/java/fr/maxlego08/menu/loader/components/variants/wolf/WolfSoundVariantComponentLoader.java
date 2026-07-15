package fr.maxlego08.menu.loader.components.variants.wolf;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.wolf.WolfSoundVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Wolf;

@AutoComponentLoader
@SinceVersion("1.20.5")
public final class WolfSoundVariantComponentLoader extends RegistryVariantLoader<Wolf.SoundVariant> {

    public WolfSoundVariantComponentLoader() {
        super("wolf/sound-variant", RegistryKey.WOLF_SOUND_VARIANT, WolfSoundVariantComponent::new);
    }
}
