package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.itemstack.ContainerSlot;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ContainerComponent(
    List<@NotNull ContainerSlot> contents
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BlockStateMeta.class, blockStateMeta -> {
            if (blockStateMeta instanceof Container container) {
                Inventory inventory = container.getInventory();

                for (ContainerSlot containerSlot : this.contents) {
                    MenuItemStack menuItemStack = containerSlot.itemStack();
                    int slot = containerSlot.slot();
                    ItemStack builtItemStack = menuItemStack.build(player);
                    inventory.setItem(slot, builtItemStack);
                }

                container.update();
                blockStateMeta.setBlockState(container);
            }
        });
        if (!apply) {
            if (Configuration.enableDebug)
                Logger.info("Failed to apply ContainerComponent to itemStack: " + itemStack.getType().name()+". This item does not support block state meta.");
        }
    }
}
