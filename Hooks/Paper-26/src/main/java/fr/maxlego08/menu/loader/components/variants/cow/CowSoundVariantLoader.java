package fr.maxlego08.menu.loader.components.variants.cow;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.cow.CowSoundVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Cow;

@AutoComponentLoader
@SinceVersion("26.1")
public final class CowSoundVariantLoader extends RegistryVariantLoader<Cow.SoundVariant> {
    public CowSoundVariantLoader() {
        super("cow/sound-variant", RegistryKey.COW_SOUND_VARIANT, CowSoundVariantComponent::new);
    }
}
