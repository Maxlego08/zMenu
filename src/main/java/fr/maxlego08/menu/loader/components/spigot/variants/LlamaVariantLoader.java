package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import fr.maxlego08.menu.test.common.interfaces.VariantComponent;
import org.bukkit.entity.Llama;

@ComponentLoader
@SinceVersion("1.11")
public class LlamaVariantLoader extends EnumVariantLoader<Llama.Color> {
    public LlamaVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("llama/variant", Llama.Color.class, variantFactory::createLlama);
    }
}
