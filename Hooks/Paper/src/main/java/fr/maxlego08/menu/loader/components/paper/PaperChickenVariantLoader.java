package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.PaperRegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Chicken;

@AutoComponentLoader
@PaperOnly
@SinceVersion("1.21.5")
public class PaperChickenVariantLoader extends PaperRegistryVariantLoader<Chicken.Variant> {
    public PaperChickenVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("chicken/variant", RegistryKey.CHICKEN_VARIANT, variantFactory::createChicken);
    }
}
