package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Llama;

@AutoComponentLoader
@SinceVersion("1.11")
public class LlamaVariantLoader extends EnumVariantLoader<Llama.Color> {
    public LlamaVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("llama/variant", Llama.Color.class, variantFactory::createLlama);
    }
}
