package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CustomModelDataComponent extends ItemComponent {
    private final @NotNull Optional<@NotNull List<@NotNull Color>> colors;
    private final @NotNull Optional<@NotNull List<@NotNull Boolean>> flags;
    private final @NotNull Optional<@NotNull List<@NotNull Float>> floats;
    private final @NotNull Optional<@NotNull List<@NotNull String>> string;

    public CustomModelDataComponent(@NotNull Optional<@NotNull List<@NotNull Color>> colors, @NotNull Optional<@NotNull List<@NotNull Boolean>> flags, @NotNull Optional<@NotNull List<@NotNull Float>> floats, @NotNull Optional<@NotNull List<@NotNull String>> string) {
        this.colors = colors;
        this.flags = flags;
        this.floats = floats;
        this.string = string;
    }

    public @NotNull Optional<@NotNull List<@NotNull Color>> getColors() {
        return colors;
    }

    public @NotNull Optional<@NotNull List<@NotNull Boolean>> getFlags() {
        return flags;
    }

    public @NotNull Optional<@NotNull List<@NotNull Float>> getFloats() {
        return floats;
    }

    public @NotNull Optional<@NotNull List<@NotNull String>> getString() {
        return string;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
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
