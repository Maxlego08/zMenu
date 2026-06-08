package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import org.bukkit.Art;
import org.bukkit.Registry;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class PaintingVariantLoader extends RegistryVariantLoader<Art> {
    public PaintingVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("painting/variant", Registry.ART, variantFactory::createPainting);
    }
}
