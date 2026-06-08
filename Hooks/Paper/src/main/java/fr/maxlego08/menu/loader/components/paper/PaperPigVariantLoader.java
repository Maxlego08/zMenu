package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.PaperRegistryVariantLoader;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Pig;

@AutoComponentLoader
@PaperOnly
@SinceVersion("1.21.5")
public class PaperPigVariantLoader extends PaperRegistryVariantLoader<Pig.Variant> {
    public PaperPigVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("pig/variant", RegistryKey.PIG_VARIANT, variantFactory::createPig);
    }
}
