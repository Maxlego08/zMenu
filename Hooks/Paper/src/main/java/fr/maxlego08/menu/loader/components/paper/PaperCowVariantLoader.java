package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.PaperRegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Cow;

@AutoComponentLoader
@PaperOnly
@SinceVersion("1.21.5")
public class PaperCowVariantLoader extends PaperRegistryVariantLoader<Cow.Variant> {
    public PaperCowVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("cow/variant", RegistryKey.COW_VARIANT, variantFactory::createCow);
    }
}
