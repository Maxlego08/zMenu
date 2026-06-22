package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotDecorations;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperPotDecorationsComponent extends ItemComponent {
    private static final int SIDES = 4;

    private final ResolvableRegistryEntry<ItemType> @NotNull [] sides;

    public PaperPotDecorationsComponent(@NotNull ResolvableRegistryEntry<ItemType> @NotNull [] sides) {
        this.sides = sides;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemType[] types = new ItemType[SIDES];
        for (int i = 0; i < SIDES; i++) {
            if (this.sides[i] == null) return;
            ItemType resolved = this.sides[i].resolve(context);
            if (resolved == null) return;
            types[i] = resolved;
        }
        itemStack.setData(DataComponentTypes.POT_DECORATIONS, PotDecorations.potDecorations(types[0], types[1], types[2], types[3]));
    }
}
