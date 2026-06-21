package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class SpigotConsumableComponent extends ItemComponent {
    private final ResolvableFloat consumeSeconds;
    private final ResolvableEnum<ConsumableComponent.Animation> animation;
    private final ResolvableSound consumeSound;
    private final ResolvableBoolean hasConsumeParticles;
    private final List<ConsumableEffect> effects;

    public SpigotConsumableComponent(float consumeSeconds, ConsumableComponent.Animation animation, Sound consumeSound, boolean hasConsumeParticles, List<ConsumableEffect> effects) {
        this.consumeSeconds = ResolvableFloat.of(consumeSeconds);
        this.animation = ResolvableEnum.of(ConsumableComponent.Animation.class, animation);
        this.consumeSound = ResolvableSound.of(consumeSound);
        this.hasConsumeParticles = ResolvableBoolean.of(hasConsumeParticles);
        this.effects = effects;
    }

    public SpigotConsumableComponent(@Nullable ResolvableFloat consumeSeconds, @Nullable ResolvableEnum<ConsumableComponent.Animation> animation, @Nullable ResolvableSound consumeSound, @Nullable ResolvableBoolean hasConsumeParticles, @NotNull List<ConsumableEffect> effects) {
        this.consumeSeconds = consumeSeconds;
        this.animation = animation;
        this.consumeSound = consumeSound;
        this.hasConsumeParticles = hasConsumeParticles;
        this.effects = effects;
    }

    public ResolvableFloat getConsumeSeconds() {
        return this.consumeSeconds;
    }

    public ResolvableEnum<ConsumableComponent.Animation> getAnimation() {
        return this.animation;
    }

    public ResolvableSound getConsumeSound() {
        return this.consumeSound;
    }

    public ResolvableBoolean getHasConsumeParticles() {
        return this.hasConsumeParticles;
    }

    public List<ConsumableEffect> getEffects() {
        return this.effects;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.consumable.ConsumableComponent consumable = itemMeta.getConsumable();

            this.applyResolvable(context, consumable::setConsumeParticles, this.hasConsumeParticles);

            this.applyResolvable(context, consumable::setConsumeSeconds, this.consumeSeconds);

            this.applyResolvable(context, consumable::setAnimation, this.animation);

            this.applyResolvable(context, consumable::setSound, this.consumeSound);
            
            consumable.setEffects(this.effects);
            itemStack.setItemMeta(itemMeta);
        }
    }
}
