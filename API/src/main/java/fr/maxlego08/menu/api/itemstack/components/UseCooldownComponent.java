package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class UseCooldownComponent extends ItemComponent {
    private final float cooldownSeconds;
    private final @Nullable NamespacedKey cooldownGroup;

    public UseCooldownComponent(float cooldownSeconds, @Nullable NamespacedKey cooldownGroup) {
        this.cooldownSeconds = cooldownSeconds;
        this.cooldownGroup = cooldownGroup;
    }

    public float getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public @Nullable NamespacedKey getCooldownGroup() {
        return this.cooldownGroup;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            org.bukkit.inventory.meta.components.UseCooldownComponent useCooldown = itemMeta.getUseCooldown();

            useCooldown.setCooldownSeconds(this.cooldownSeconds);
            useCooldown.setCooldownGroup(this.cooldownGroup);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
