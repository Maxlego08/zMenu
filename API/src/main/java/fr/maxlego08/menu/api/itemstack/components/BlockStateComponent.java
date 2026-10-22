package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class BlockStateComponent extends ItemComponent {
    private final @NotNull ResolvableString resolvableBlockState;

    public BlockStateComponent(@NotNull ResolvableString resolvableBlockState) {
        this.resolvableBlockState = resolvableBlockState;
    }

    public @NotNull ResolvableString getResolvableBlockState() {
        return this.resolvableBlockState;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        try {
            String resolvedBlocState = Resolvable.resolve(context, this.resolvableBlockState);
            BlockData blockData = Bukkit.createBlockData(itemStack.getType(), resolvedBlocState);

            boolean apply = ItemUtil.editMeta(itemStack, BlockDataMeta.class, meta -> meta.setBlockData(blockData));
            if (!apply){
                Logger.info("Failed to apply BlockData to ItemStack of type "+itemStack.getType().name());
            }
        } catch (IllegalArgumentException e) {
            if (Configuration.enableDebug)
                Logger.info("Invalid block state '" + this.resolvableBlockState + "' for item type " + itemStack.getType().name());
        }
    }

    /**
     * The loader joins the section's {@code key: value} entries into {@code [key=value, key2=value2]},
     * so the section is read back from that string.
     */
    @Override
    public @Nullable Object serialize() {
        Object serialized = this.resolvableBlockState.serialize();
        if (!(serialized instanceof String blockState)) {
            throw new UnsupportedOperationException("The block state " + serialized + " cannot be serialized");
        }
        if (blockState.startsWith("[")) blockState = blockState.substring(1);
        if (blockState.endsWith("]")) blockState = blockState.substring(0, blockState.length() - 1);

        Map<String, Object> map = new LinkedHashMap<>();
        for (String entry : blockState.split(",")) {
            int separator = entry.indexOf('=');
            if (separator <= 0) {
                throw new UnsupportedOperationException("The block state entry '" + entry + "' cannot be serialized");
            }
            map.put(entry.substring(0, separator).trim(), entry.substring(separator + 1).trim());
        }
        return map;
    }
}
