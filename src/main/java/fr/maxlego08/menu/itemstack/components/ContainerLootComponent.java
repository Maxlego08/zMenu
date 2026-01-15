package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ContainerLootComponent(
    @NotNull LootTables lootable,
    long seed
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BlockStateMeta.class, blockStateMeta -> {
            if (blockStateMeta instanceof Lootable lootable){
                lootable.setLootTable(this.lootable.getLootTable());
                lootable.setSeed(this.seed);
            }
        });
        if (!apply) {
            if (Configuration.enableDebug)
                Logger.info("Failed to apply ContainerLootComponent to itemStack: " + itemStack.getType().name()+". This item does not support block state meta.");
        }
    }
}
