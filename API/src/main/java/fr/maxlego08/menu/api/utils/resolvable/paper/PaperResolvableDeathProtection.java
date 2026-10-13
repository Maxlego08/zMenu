package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import io.papermc.paper.datacomponent.item.DeathProtection;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PaperResolvableDeathProtection implements Resolvable<DeathProtection> {
    private final List<PaperResolvableConsumeEffect> effectsResolvable;

    public PaperResolvableDeathProtection(List<PaperResolvableConsumeEffect> effectsResolvable) {
        this.effectsResolvable = effectsResolvable;
    }

    @Override
    public @Nullable DeathProtection resolve(@NotNull BuildContext context) {
        DeathProtection.Builder builder = DeathProtection.deathProtection();

        Resolvable.applyResolvable(context, this.effectsResolvable, builder::addEffects);

        return builder.build();
    }

    /**
     * Writes the section the death protection loader reads: {@code death_effects} as a list of consume effect maps.
     */
    @Override
    public @NotNull Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("death-effects", Resolvable.serializeList(this.effectsResolvable));
        return map;
    }
}
