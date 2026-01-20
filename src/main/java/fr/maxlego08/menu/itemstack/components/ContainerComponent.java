package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
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

@SuppressWarnings("unused")
public class ContainerComponent extends ItemComponent {
    private final List<@NotNull ContainerSlot> contents;

    public ContainerComponent(List<@NotNull ContainerSlot> contents) {
        this.contents = contents;
    }

    public List<@NotNull ContainerSlot> getContents() {
        return this.contents;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        //TODO: Trouver pq ca ajoute pas les items dans le container
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
