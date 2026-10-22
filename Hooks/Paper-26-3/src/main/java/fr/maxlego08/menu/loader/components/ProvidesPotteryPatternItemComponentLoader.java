package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.components.ProvidesPotteryPatternComponent;
import fr.maxlego08.menu.loader.components.variants.base.RegistryVariantLoader;
import io.papermc.paper.block.pot.PotPatternType;
import io.papermc.paper.registry.RegistryKey;

@AutoComponentLoader
@SinceVersion("26.3")
public final class ProvidesPotteryPatternItemComponentLoader extends RegistryVariantLoader<PotPatternType> {

    public ProvidesPotteryPatternItemComponentLoader() {
        super("provides-pottery-pattern", RegistryKey.DECORATED_POT_PATTERN, ProvidesPotteryPatternComponent::new);
    }
}
