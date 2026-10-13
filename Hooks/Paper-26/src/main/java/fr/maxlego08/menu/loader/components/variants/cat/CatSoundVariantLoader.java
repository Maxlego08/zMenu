package fr.maxlego08.menu.loader.components.variants.cat;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.cat.CatSoundVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Cat;

@AutoComponentLoader
@SinceVersion("26.1")
public final class CatSoundVariantLoader extends RegistryVariantLoader<Cat.SoundVariant> {
    public CatSoundVariantLoader() {
        super("cat/sound-variant", RegistryKey.CAT_SOUND_VARIANT, CatSoundVariantComponent::new);
    }
}
