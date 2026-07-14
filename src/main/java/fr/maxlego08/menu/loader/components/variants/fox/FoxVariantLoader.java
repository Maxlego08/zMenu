package fr.maxlego08.menu.loader.components.variants.fox;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.fox.FoxVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Fox;

@AutoComponentLoader
@SinceVersion("1.13")
public class FoxVariantLoader extends EnumVariantLoader<Fox.Type> {
    public FoxVariantLoader() {
        super("fox/variant", Fox.Type.class, FoxVariantComponent::new);
    }
}
