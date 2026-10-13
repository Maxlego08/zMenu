package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotDecorations;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PotDecorationsComponent extends ItemComponent {
    protected static final int SIDES = 4;

    private final ResolvableRegistryEntry<ItemType> @NotNull [] sides;

    public PotDecorationsComponent(@NotNull ResolvableRegistryEntry<ItemType> @NotNull [] sides) {
        this.sides = sides;
    }

    /**
     * @return the sides in the order back, left, right, front
     */
    public ResolvableRegistryEntry<ItemType> @NotNull [] getSides() {
        return this.sides;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemType[] types = this.resolveSides(context);
        if (types == null) return;
        itemStack.setData(DataComponentTypes.POT_DECORATIONS, PotDecorations.potDecorations(types[0], types[1], types[2], types[3]));
    }

    protected ItemType @Nullable [] resolveSides(@NotNull BuildContext context) {
        ItemType[] types = new ItemType[SIDES];
        for (int i = 0; i < SIDES; i++) {
            if (this.sides[i] == null) return null;
            ItemType resolved = this.sides[i].resolve(context);
            if (resolved == null) return null;
            types[i] = resolved;
        }
        return types;
    }

    @Override
    public @Nullable Object serialize() {
        return Resolvable.serializeList(Arrays.asList(this.sides));
    }
}
