package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BlockStateComponent extends ItemComponent {
    private final @NotNull String blockState;

    public BlockStateComponent(@NotNull String blockState) {
        this.blockState = blockState;
    }

    public @NotNull String getBlockState() {
        return this.blockState;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        try {
            BlockData blockData = Bukkit.createBlockData(itemStack.getType(), this.blockState);
            boolean apply = ItemUtil.editMeta(itemStack, BlockDataMeta.class, meta -> {
                meta.setBlockData(blockData);
            });
            if (!apply){
                Logger.info("Failed to apply BlockData to ItemStack of type "+itemStack.getType().name());
            }
        } catch (IllegalArgumentException e) {
            if (Configuration.enableDebug)
                Logger.info("Invalid block state '" + this.blockState + "' for item type " + itemStack.getType().name());
        }
    }
}
