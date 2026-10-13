package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.PotDecorationsComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.block.DecoratedPot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyPotDecorationsComponent extends PotDecorationsComponent {
    private static final DecoratedPot.Side[] ORDER = {DecoratedPot.Side.BACK, DecoratedPot.Side.LEFT, DecoratedPot.Side.RIGHT, DecoratedPot.Side.FRONT};

    public LegacyPotDecorationsComponent(@NotNull ResolvableRegistryEntry<ItemType> @NotNull [] sides) {
        super(sides);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemType[] types = this.resolveSides(context);
        if (types == null) return;

        boolean apply = ItemUtil.editMeta(itemStack, BlockStateMeta.class, blockStateMeta -> {
            if (blockStateMeta.getBlockState() instanceof DecoratedPot decoratedPot) {
                for (int i = 0; i < SIDES; i++) {
                    decoratedPot.setSherd(ORDER[i], types[i].asMaterial());
                }
                blockStateMeta.setBlockState(decoratedPot);
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply PotDecorationsComponent to item: " + itemStack.getType().name());
    }
}
