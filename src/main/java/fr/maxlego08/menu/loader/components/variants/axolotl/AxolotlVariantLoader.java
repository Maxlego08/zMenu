package fr.maxlego08.menu.loader.components.variants.axolotl;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.axolotl.AxolotlVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Axolotl;

@AutoComponentLoader
@SinceVersion("1.17")
public class AxolotlVariantLoader extends EnumVariantLoader<Axolotl.Variant> {
    public AxolotlVariantLoader() {
        super("axolotl/variant", Axolotl.Variant.class, AxolotlVariantComponent::new);
    }
}
