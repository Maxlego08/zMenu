package fr.maxlego08.menu.loader.components.variants.llama;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.llama.LlamaVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Llama;

@AutoComponentLoader
@SinceVersion("1.11")
public class LlamaVariantLoader extends EnumVariantLoader<Llama.Color> {
    public LlamaVariantLoader() {
        super("llama/variant", Llama.Color.class, LlamaVariantComponent::new);
    }
}
