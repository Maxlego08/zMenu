package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.util.TriState;
import org.bukkit.block.BlockType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public record ResolvableToolRule(
        @NotNull Resolvable<RegistryKeySet<BlockType>> blocks,
        @Nullable ResolvableFloat speed,
        @Nullable ResolvableBoolean correctForDrops
) implements Resolvable<Tool.Rule> {

    @Override
    public @Nullable Tool.Rule resolve(@NotNull BuildContext context) {
        RegistryKeySet<BlockType> resolvedBlocks = this.blocks.resolve(context);
        if (resolvedBlocks == null) return null;
        Float resolvedSpeed = Resolvable.resolve(context, this.speed);
        Boolean resolvedCorrectForDrops = Resolvable.resolve(context, this.correctForDrops);
        return Tool.rule(resolvedBlocks, resolvedSpeed, resolvedCorrectForDrops == null ? TriState.NOT_SET : TriState.byBoolean(resolvedCorrectForDrops));
    }

    /**
     * Writes {@code blocks}, then {@code speed} and {@code correct-for-drops} when they are set.
     */
    @Override
    public @NotNull Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("blocks", this.blocks.serialize());
        if (this.speed != null) map.put("speed", this.speed.serialize());
        if (this.correctForDrops != null) map.put("correct-for-drops", this.correctForDrops.serialize());
        return map;
    }
}
