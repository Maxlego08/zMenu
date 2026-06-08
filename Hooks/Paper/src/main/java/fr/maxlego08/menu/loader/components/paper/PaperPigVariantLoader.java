package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.loader.components.variants.base.PaperRegistryVariantLoader;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Pig;

@ComponentLoader
@PaperOnly
@SinceVersion("1.21.5")
public class PaperPigVariantLoader extends PaperRegistryVariantLoader<Pig.Variant> {
    public PaperPigVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("pig/variant", RegistryKey.PIG_VARIANT, variantFactory::createPig);
    }
}
