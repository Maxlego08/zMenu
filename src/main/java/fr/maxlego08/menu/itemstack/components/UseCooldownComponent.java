package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record UseCooldownComponent(
    float cooldownSeconds,
    @Nullable NamespacedKey cooldownGroup
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            org.bukkit.inventory.meta.components.UseCooldownComponent useCooldown = itemMeta.getUseCooldown();

            useCooldown.setCooldownSeconds(this.cooldownSeconds);
            useCooldown.setCooldownGroup(this.cooldownGroup);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
