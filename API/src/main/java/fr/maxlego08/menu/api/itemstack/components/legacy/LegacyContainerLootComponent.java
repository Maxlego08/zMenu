package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.ContainerLootComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableLong;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyContainerLootComponent extends ContainerLootComponent {

    public LegacyContainerLootComponent(@Nullable ResolvableEnum<LootTables> resolvableLootTable, @NotNull ResolvableLong seed) {
        super(resolvableLootTable, seed);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BlockStateMeta.class, blockStateMeta -> {
            BlockState blockState = blockStateMeta.getBlockState();
            if (blockState instanceof Lootable lootable) {

                Resolvable.applyResolvable(context, this.getResolvableLootTable(), lootTables -> {
                    lootable.setLootTable(lootTables.getLootTable());
                });

                Resolvable.applyResolvable(context, this.getSeed(), lootable::setSeed);

                blockStateMeta.setBlockState(blockState);
            }
        });
        if (!apply && Configuration.enableDebug) {
            Logger.info("Failed to apply ContainerLootComponent to itemStack: " + itemStack.getType().name() + ". This item does not support block state meta.");
        }
    }
}
