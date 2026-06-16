package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.DyeColorVariantLoader;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SheepColorVariantLoader extends DyeColorVariantLoader {
    public SheepColorVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("sheep/color", variantFactory::createSheep);
    }
}
