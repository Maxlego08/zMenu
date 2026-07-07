package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BaseColorComponent extends ItemComponent {
    private final @NotNull Resolvable<DyeColor> baseColorResolvable;

    public BaseColorComponent(@NotNull Resolvable<DyeColor> baseColorResolvable) {
        this.baseColorResolvable = baseColorResolvable;
    }

    public @NotNull Resolvable<DyeColor> getBaseColorResolvable() {
        return this.baseColorResolvable;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.baseColorResolvable, color -> itemStack.setData(DataComponentTypes.BASE_COLOR, color));
    }
}
