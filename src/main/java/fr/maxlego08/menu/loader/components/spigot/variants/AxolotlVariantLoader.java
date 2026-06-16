package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Axolotl;

@AutoComponentLoader
@SinceVersion("1.17")
public class AxolotlVariantLoader extends EnumVariantLoader<Axolotl.Variant> {
    public AxolotlVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("axolotl/variant", Axolotl.Variant.class, variantFactory::createAxolotl);
    }
}
