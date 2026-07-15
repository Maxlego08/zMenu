package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.paper.PaperConsumableComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperResolvableConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.21.2")
@PaperOnly
public class PaperConsumableItemComponentLoader extends AbstractEffectItemComponentLoader {

    public PaperConsumableItemComponentLoader() {
        super("consumable");
    }


    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableFloat consumeSeconds = this.asResolvableFloat(componentSection, "consume-seconds", 1.6f);
        Resolvable<ItemUseAnimation> animation = this.asResolvableEnum(componentSection, "animation", ItemUseAnimation.class, ItemUseAnimation.EAT);
        ResolvableNamespacedKey consumeSound = this.asResolvableKey(componentSection, "consume-sound", NamespacedKey.fromString("minecraft:entity.generic.eat"));
        ResolvableBoolean hasConsumeParticles = this.asResolvableBoolean(componentSection, "has-consume-particles",true);
        List<PaperResolvableConsumeEffect> effects = this.parseEffects(componentSection.getMapList("on-consume-effects"));

        return effects.isEmpty() ? null : new PaperConsumableComponent(
                consumeSeconds,
                animation,
                consumeSound,
                hasConsumeParticles,
                effects
        );
    }
}
