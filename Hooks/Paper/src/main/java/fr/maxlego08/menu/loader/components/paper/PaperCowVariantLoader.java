package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.loader.components.variants.base.PaperRegistryVariantLoader;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Cow;

@ComponentLoader
@PaperOnly
@SinceVersion("1.21.5")
public class PaperCowVariantLoader extends PaperRegistryVariantLoader<Cow.Variant> {
    public PaperCowVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("cow/variant", RegistryKey.COW_VARIANT, variantFactory::createCow);
    }
}
