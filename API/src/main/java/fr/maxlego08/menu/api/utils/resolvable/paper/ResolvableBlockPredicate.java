package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import io.papermc.paper.block.BlockPredicate;
import org.bukkit.block.BlockType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class ResolvableBlockPredicate implements Resolvable<BlockPredicate> {
    private final TypedKeySetResolvable<BlockType> blockTypes;

    public ResolvableBlockPredicate(TypedKeySetResolvable<BlockType> blockTypes) {
        this.blockTypes = blockTypes;
    }

    @Override
    public @NonNull BlockPredicate resolve(@NotNull BuildContext context) {
        BlockPredicate.Builder builder = BlockPredicate.predicate();

        Resolvable.applyResolvable(context, this.blockTypes, builder::blocks);

        return builder.build();
    }
}
