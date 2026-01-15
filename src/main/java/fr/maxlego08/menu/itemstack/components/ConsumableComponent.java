package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ConsumableComponent(
    float consumeSeconds,
    org.bukkit.inventory.meta.components.consumable.ConsumableComponent.Animation animation,
    Sound consumeSound,
    boolean hasConsumeParticles,
    List<ConsumableEffect> effects
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
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
