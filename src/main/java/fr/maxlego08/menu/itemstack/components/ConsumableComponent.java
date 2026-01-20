package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class ConsumableComponent extends ItemComponent {
    private final float consumeSeconds;
    private final org.bukkit.inventory.meta.components.consumable.ConsumableComponent.Animation animation;
    private final Sound consumeSound;
    private final boolean hasConsumeParticles;
    private final List<ConsumableEffect> effects;

    public ConsumableComponent(float consumeSeconds, org.bukkit.inventory.meta.components.consumable.ConsumableComponent.Animation animation, Sound consumeSound, boolean hasConsumeParticles, List<ConsumableEffect> effects) {
        this.consumeSeconds = consumeSeconds;
        this.animation = animation;
        this.consumeSound = consumeSound;
        this.hasConsumeParticles = hasConsumeParticles;
        this.effects = effects;
    }

    public float getConsumeSeconds() {
        return consumeSeconds;
    }

    public org.bukkit.inventory.meta.components.consumable.ConsumableComponent.Animation getAnimation() {
        return animation;
    }

    public Sound getConsumeSound() {
        return consumeSound;
    }

    public boolean isHasConsumeParticles() {
        return hasConsumeParticles;
    }

    public List<ConsumableEffect> getEffects() {
        return effects;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.consumable.ConsumableComponent consumable = itemMeta.getConsumable();
            consumable.setConsumeParticles(this.hasConsumeParticles);
            consumable.setConsumeSeconds(this.consumeSeconds);
            consumable.setAnimation(this.animation);
            consumable.setSound(this.consumeSound);
            consumable.setEffects(this.effects);
            itemStack.setItemMeta(itemMeta);
        }
    }
}
