package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ConsumableComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperResolvableConsumeEffect;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.21.3")
public class ConsumableItemComponentLoader extends AbstractEffectItemComponentLoader {

    public ConsumableItemComponentLoader() {
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

        return effects.isEmpty() ? null : new ConsumableComponent(
                consumeSeconds,
                animation,
                consumeSound,
                hasConsumeParticles,
                effects
        );
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Consumable consumable = itemStack.getData(DataComponentTypes.CONSUMABLE);
        if (consumable == null || consumable.consumeEffects().isEmpty()) return null;
        NamespacedKey sound = NamespacedKey.fromString(consumable.sound().asString());
        if (sound == null) return null;
        List<PaperResolvableConsumeEffect> effects = new ArrayList<>();
        for (ConsumeEffect consumeEffect : consumable.consumeEffects()) {
            PaperResolvableConsumeEffect effect = PaperResolvableConsumeEffect.of(consumeEffect);
            if (effect == null) return null;
            effects.add(effect);
        }
        return new ConsumableComponent(
                ResolvableFloat.of(consumable.consumeSeconds()),
                ResolvableEnum.of(ItemUseAnimation.class, consumable.animation()),
                ResolvableNamespacedKey.of(sound),
                ResolvableBoolean.of(consumable.hasConsumeParticles()),
                effects
        );
    }
}
