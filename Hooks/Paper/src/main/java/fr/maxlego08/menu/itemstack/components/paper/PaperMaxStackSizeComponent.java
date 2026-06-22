package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.MaxStackSizeComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperMaxStackSizeComponent extends MaxStackSizeComponent {

    public PaperMaxStackSizeComponent(int maxStackSize) {
        super(maxStackSize);
    }

    public PaperMaxStackSizeComponent(ResolvableInt maxStackSize) {
        super(maxStackSize);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.maxStackSize, resolved -> itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, resolved));
    }
}
