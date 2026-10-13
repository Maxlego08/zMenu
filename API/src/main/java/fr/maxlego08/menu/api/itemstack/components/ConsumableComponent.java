package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperResolvableConsumeEffect;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConsumableComponent extends ItemComponent {
    private final ResolvableFloat consumeSeconds;
    private final Resolvable<ItemUseAnimation> animation;
    private final ResolvableNamespacedKey sound;
    private final ResolvableBoolean hasConsumeParticles;
    private final List<PaperResolvableConsumeEffect> effects;

    public ConsumableComponent(ResolvableFloat consumeSeconds, Resolvable<ItemUseAnimation> animation, ResolvableNamespacedKey sound, ResolvableBoolean hasConsumeParticles, List<PaperResolvableConsumeEffect> effects) {
        this.consumeSeconds = consumeSeconds;
        this.animation = animation;
        this.sound = sound;
        this.hasConsumeParticles = hasConsumeParticles;
        this.effects = effects;
    }


    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {

        Consumable.Builder consumable = Consumable.consumable();

        this.applyResolvable(context, consumable::consumeSeconds, this.consumeSeconds);
        this.applyResolvable(context, consumable::animation, this.animation);
        this.applyResolvable(context, consumable::sound, this.sound);
        this.applyResolvable(context, consumable::hasConsumeParticles, this.hasConsumeParticles);

        for (PaperResolvableConsumeEffect effect : this.effects) {
            ConsumeEffect resolved = effect.resolve(context);
            if (resolved != null) {
                consumable.addEffect(resolved);
            }
        }

        itemStack.setData(DataComponentTypes.CONSUMABLE, consumable.build());
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        Object consumeSeconds = this.consumeSeconds.serialize();
        if (!Float.valueOf(1.6f).equals(consumeSeconds)) map.put("consume-seconds", consumeSeconds);
        Object animation = this.animation.serialize();
        if (!ItemUseAnimation.EAT.name().equals(animation)) map.put("animation", animation);
        Object sound = this.sound.serialize();
        if (!"minecraft:entity.generic.eat".equals(sound)) map.put("consume-sound", sound);
        Object hasConsumeParticles = this.hasConsumeParticles.serialize();
        if (!Boolean.TRUE.equals(hasConsumeParticles)) map.put("has-consume-particles", hasConsumeParticles);
        map.put("on-consume-effects", Resolvable.serializeList(this.effects));
        return map;
    }
}
