package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Fox;

@ComponentLoader
@SinceVersion("1.13")
public class FoxVariantLoader extends EnumVariantLoader<Fox.Type> {
    public FoxVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("fox/variant", Fox.Type.class, variantFactory::createFox);
    }
}
