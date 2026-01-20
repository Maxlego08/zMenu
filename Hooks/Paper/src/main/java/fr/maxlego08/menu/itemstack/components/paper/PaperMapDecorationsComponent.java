package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapDecorations;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PaperMapDecorationsComponent extends ItemComponent {
    private final Map<@NotNull String, MapDecorations.DecorationEntry> decorations;

    public PaperMapDecorationsComponent(@NotNull Map<@NotNull String, MapDecorations.DecorationEntry> decorations){
        this.decorations = decorations;
    }
    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.MAP_DECORATIONS,MapDecorations.mapDecorations(this.decorations));
    }
}
