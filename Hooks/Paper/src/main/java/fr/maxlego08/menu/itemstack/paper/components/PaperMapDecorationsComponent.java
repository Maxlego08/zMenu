package fr.maxlego08.menu.itemstack.paper.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapDecorations;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record PaperMapDecorationsComponent(
    @NotNull Map<@NotNull String, MapDecorations.DecorationEntry> decorations
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.MAP_DECORATIONS,MapDecorations.mapDecorations(this.decorations));
    }
}
