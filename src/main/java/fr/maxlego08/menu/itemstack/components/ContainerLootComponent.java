package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
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

@SuppressWarnings("unused")
public class ContainerLootComponent extends ItemComponent {

    private final @NotNull LootTables lootable;
    private final long seed;

    public ContainerLootComponent(@NotNull LootTables lootable, long seed) {
        this.lootable = lootable;
        this.seed = seed;
    }

    public @NotNull LootTables getLootable() {
        return this.lootable;
    }

    public long getSeed() {
        return this.seed;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BlockStateMeta.class, blockStateMeta -> {
            if (blockStateMeta instanceof Lootable lootableMeta) {
                lootableMeta.setLootTable(this.lootable.getLootTable());
                lootableMeta.setSeed(this.seed);
            }
        });
        if (!apply && Configuration.enableDebug) {
            Logger.info("Failed to apply ContainerLootComponent to itemStack: " + itemStack.getType().name() + ". This item does not support block state meta.");
        }
    }

}
