package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotDecorations;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PaperPotDecorationsComponent(
    @Nullable ItemType back,
    @Nullable ItemType left,
    @Nullable ItemType right,
    @Nullable ItemType front
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.POT_DECORATIONS, PotDecorations.potDecorations(this.back, this.left, this.right, this.front));
    }
}
