package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapItemColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class MapColorComponent extends ItemComponent {

    private final @NotNull ResolvableColor color;

    public MapColorComponent(@NotNull ResolvableColor color) {
        this.color = color;
    }

    public @NotNull ResolvableColor getColor() {
        return this.color;
    }

    @Override
    public @Nullable Object serialize() {
        return this.color.serialize();
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        this.applyResolvable(context, color -> itemStack.setData(DataComponentTypes.MAP_COLOR, MapItemColor.mapItemColor().color(color).build()), this.color);
    }

}
