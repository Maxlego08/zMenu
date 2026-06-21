package fr.maxlego08.menu.itemstack.components.spigot;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.MaxStackSizeComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class SpigotMaxStackSizeComponent extends MaxStackSizeComponent {

    public SpigotMaxStackSizeComponent(int maxStackSize) {
        super(maxStackSize);
    }

    public SpigotMaxStackSizeComponent(ResolvableInt maxStackSize) {
        super(maxStackSize);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        this.applyResolvable(context, itemMeta::setMaxStackSize, this.maxStackSize);

        itemStack.setItemMeta(itemMeta);
    }
}

