package fr.maxlego08.menu.loader.components.spigot.variants;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Rabbit;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class RabbitVariantLoader extends EnumVariantLoader<Rabbit.Type> {
    public RabbitVariantLoader(MenuPlugin plugin, VariantComponent variantFactory) {
        super("rabbit/variant", Rabbit.Type.class, variantFactory::createRabbit);
    }
}
