package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (!Objects.equals(this.cooldownSeconds.serialize(), 0f)) map.put("seconds", this.cooldownSeconds.serialize());
        if (this.cooldownGroup != null) map.put("cooldown-group", this.cooldownGroup.serialize());
        return map;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Float seconds = this.cooldownSeconds.resolve(context);
        if (seconds == null) return;

        UseCooldown.Builder useCooldown = UseCooldown.useCooldown(seconds);

        Resolvable.applyResolvable(context, this.cooldownGroup, useCooldown::cooldownGroup);

        itemStack.setData(DataComponentTypes.USE_COOLDOWN, useCooldown.build());
    }
}
