package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import io.papermc.paper.datacomponent.item.DeathProtection;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

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
}
