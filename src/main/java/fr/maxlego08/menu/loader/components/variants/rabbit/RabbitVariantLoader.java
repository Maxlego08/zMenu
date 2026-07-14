package fr.maxlego08.menu.loader.components.variants.rabbit;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.variants.rabbit.RabbitVariantComponent;
import fr.maxlego08.menu.loader.components.variants.base.EnumVariantLoader;
import org.bukkit.entity.Rabbit;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class RabbitVariantLoader extends EnumVariantLoader<Rabbit.Type> {
    public RabbitVariantLoader() {
        super("rabbit/variant", Rabbit.Type.class, RabbitVariantComponent::new);
    }
}
