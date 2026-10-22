package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ResolvableItemDamageFunction implements Resolvable<ItemDamageFunction> {
    private final ResolvableFloat threshold;
    private final ResolvableFloat base;
    private final ResolvableFloat factor;

    public ResolvableItemDamageFunction(ResolvableFloat threshold, ResolvableFloat base, ResolvableFloat factor) {
        this.threshold = threshold;
        this.base = base;
        this.factor = factor;
    }

    @Override
    public @NonNull ItemDamageFunction resolve(@NotNull BuildContext context) {
        ItemDamageFunction.Builder builder = ItemDamageFunction.itemDamageFunction();

        Resolvable.applyResolvable(context, this.threshold, builder::threshold);
        Resolvable.applyResolvable(context, this.base, builder::base);
        Resolvable.applyResolvable(context, this.factor, builder::factor);

        return builder.build();
    }

    @Override
    public @NotNull Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (this.threshold != null) map.put("threshold", this.threshold.serialize());
        if (this.base != null) map.put("base", this.base.serialize());
        if (this.factor != null) map.put("factor", this.factor.serialize());
        return map;
    }
}
