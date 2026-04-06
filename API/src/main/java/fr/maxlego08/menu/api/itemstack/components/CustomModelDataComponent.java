package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomModelDataComponent extends ItemComponent {
    private final @NotNull List<@NotNull Color> colors;
    private final @NotNull List<@NotNull Boolean> flags;
    private final @NotNull List<@NotNull Float> floats;
    private final @NotNull List<@NotNull String> strings;

    public CustomModelDataComponent(@NotNull List<@NotNull Color> colors, @NotNull List<@NotNull Boolean> flags, @NotNull List<@NotNull Float> floats, @NotNull List<@NotNull String> strings) {
        this.colors = colors;
        this.flags = flags;
        this.floats = floats;
        this.strings = strings;
    }

    public @NotNull List<@NotNull Color> getColors() {
        return colors;
    }

    public @NotNull List<@NotNull Boolean> getFlags() {
        return this.flags;
    }

    public @NotNull List<@NotNull Float> getFloats() {
        return this.floats;
    }

    public @NotNull List<@NotNull String> getStrings() {
        return this.strings;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.CustomModelDataComponent customModelDataComponent = itemMeta.getCustomModelDataComponent();

            if (!this.colors.isEmpty()) {
                customModelDataComponent.setColors(this.colors);
            }
            if (!this.flags.isEmpty()) {
                customModelDataComponent.setFlags(this.flags);
            }
            if (!this.floats.isEmpty()) {
                customModelDataComponent.setFloats(this.floats);
            }
            if (!this.strings.isEmpty()) {
                customModelDataComponent.setStrings(this.strings);
            }

            itemStack.setItemMeta(itemMeta);
        }
    }
}
