package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record CustomModelDataComponent(
    @NotNull Optional<@NotNull List<@NotNull Color>> colors,
    @NotNull Optional<@NotNull List<@NotNull Boolean>> flags,
    @NotNull Optional<@NotNull List<@NotNull Float>> floats,
    @NotNull Optional<@NotNull List<@NotNull String>> string
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.CustomModelDataComponent customModelDataComponent = itemMeta.getCustomModelDataComponent();

            this.colors.ifPresent(customModelDataComponent::setColors);
            this.flags.ifPresent(customModelDataComponent::setFlags);
            this.floats.ifPresent(customModelDataComponent::setFloats);
            this.string.ifPresent(customModelDataComponent::setStrings);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
