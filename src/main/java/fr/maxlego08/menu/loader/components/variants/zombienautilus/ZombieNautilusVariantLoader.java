package fr.maxlego08.menu.loader.components.variants.zombienautilus;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.zombienautilus.ZombieNautilusVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.ZombieNautilus;

@AutoComponentLoader
@SinceVersion("1.21.11")
public final class ZombieNautilusVariantLoader extends RegistryVariantLoader<ZombieNautilus.Variant> {
    public ZombieNautilusVariantLoader() {
        super("zombie-nautilus/variant", RegistryKey.ZOMBIE_NAUTILUS_VARIANT, ZombieNautilusVariantComponent::new);
    }
}
