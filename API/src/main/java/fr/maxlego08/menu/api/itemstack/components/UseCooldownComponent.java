package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class UseCooldownComponent extends ItemComponent {
    private final ResolvableFloat cooldownSeconds;
    private final @Nullable ResolvableNamespacedKey cooldownGroup;

    public UseCooldownComponent(ResolvableFloat cooldownSeconds, @Nullable ResolvableNamespacedKey cooldownGroup) {
        this.cooldownSeconds = cooldownSeconds;
        this.cooldownGroup = cooldownGroup;
    }

    public ResolvableFloat getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public @Nullable ResolvableNamespacedKey getCooldownGroup() {
        return this.cooldownGroup;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            org.bukkit.inventory.meta.components.UseCooldownComponent useCooldown = itemMeta.getUseCooldown();

            Resolvable.applyResolvable(context, this.cooldownSeconds, useCooldown::setCooldownSeconds);

            Resolvable.applyResolvable(context, this.cooldownGroup, useCooldown::setCooldownGroup);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
