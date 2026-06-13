package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.annotations.SpigotOnly;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import org.bukkit.Registry;
import org.bukkit.entity.Pig;

@AutoComponentLoader
@SpigotOnly
@SinceVersion("1.21.5")
public class SpigotPigVariantLoader extends RegistryVariantLoader<Pig.Variant> {
    public SpigotPigVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("pig/variant", Registry.PIG_VARIANT, variantFactory::createPig);
    }
}
