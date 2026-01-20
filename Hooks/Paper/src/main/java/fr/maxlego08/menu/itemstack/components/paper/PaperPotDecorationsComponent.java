package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotDecorations;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperPotDecorationsComponent extends ItemComponent {
    private final PotDecorations decorations;

    public PaperPotDecorationsComponent(@NotNull PotDecorations decorations) {
        this.decorations = decorations;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.POT_DECORATIONS, decorations);
    }
}
