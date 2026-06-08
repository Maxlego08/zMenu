package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import org.bukkit.Registry;
import org.bukkit.entity.Villager;

@ComponentLoader
@SinceVersion("1.20.5")
public class VillagerVariantLoader extends RegistryVariantLoader<Villager.Type> {
    public VillagerVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("villager/variant", Registry.VILLAGER_TYPE, variantFactory::createVillager);
    }
}
