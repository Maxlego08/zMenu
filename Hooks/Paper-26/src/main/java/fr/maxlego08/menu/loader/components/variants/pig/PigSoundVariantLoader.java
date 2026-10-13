package fr.maxlego08.menu.loader.components.variants.pig;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.pig.PigSoundVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Pig;

@AutoComponentLoader
@SinceVersion("26.1")
public final class PigSoundVariantLoader extends RegistryVariantLoader<Pig.SoundVariant> {
    public PigSoundVariantLoader() {
        super("pig/sound-variant", RegistryKey.PIG_SOUND_VARIANT, PigSoundVariantComponent::new);
    }
}
