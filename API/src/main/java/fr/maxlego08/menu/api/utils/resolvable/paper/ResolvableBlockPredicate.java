package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import io.papermc.paper.block.BlockPredicate;
import org.bukkit.block.BlockType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

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

    /**
     * Writes the block key as a single string, one entry of the {@code blocks} list the can-break / can-place-on loaders read;
     * a predicate holding several keys is written as a list of them.
     */
    @Override
    public @NotNull Object serialize() {
        List<Object> keys = Resolvable.serializeList(this.blockTypes.keys());
        return keys.size() == 1 ? Objects.requireNonNull(keys.getFirst()) : keys;
    }
}
