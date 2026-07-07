package fr.maxlego08.menu.loader.components.variants.villager;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.villager.VillagerVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import org.bukkit.entity.Villager;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class VillagerVariantLoader extends RegistryVariantLoader<Villager.Type> {
    public VillagerVariantLoader() {
        super("villager/variant", Villager.Type.class, VillagerVariantComponent::new);
    }
}
