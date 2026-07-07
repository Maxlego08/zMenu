package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.itemstack.ZMusicInstrument;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import net.kyori.adventure.text.Component;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public final class ResolvableMusicInstrument implements Resolvable<MusicInstrument> {
    private final ResolvableNamespacedKey key;
    private final ResolvableFloat duration;
    private final ResolvableFloat range;
    private final ResolvableComponent description;
    private final ResolvableSound soundEvent;

    private final ResolvableRegistryEntry<MusicInstrument> instrumentEntry;

    public ResolvableMusicInstrument(@Nullable ResolvableNamespacedKey key, @Nullable ResolvableFloat duration, @Nullable ResolvableFloat range, @Nullable ResolvableComponent description, @Nullable ResolvableSound soundEvent) {
        this.key = key;
        this.duration = duration;
        this.range = range;
        this.description = description;
        this.soundEvent = soundEvent;
        this.instrumentEntry = null;
    }

    public ResolvableMusicInstrument(@NotNull ResolvableRegistryEntry<MusicInstrument> instrumentEntry) {
        this.key = null;
        this.duration = null;
        this.range = null;
        this.description = null;
        this.soundEvent = null;
        this.instrumentEntry = instrumentEntry;
    }

    @Override
    public @Nullable MusicInstrument resolve(@NotNull BuildContext context) {
        if (this.instrumentEntry != null) {
            return this.instrumentEntry.resolve(context);
        }
        NamespacedKey namespacedKey = Resolvable.resolve(context, this.key);
        Float resolvedDuration = Resolvable.resolve(context, this.duration);
        Float resolvedRange = Resolvable.resolve(context, this.range);
        Component resolvedDescription = Resolvable.resolve(context, this.description);
        Sound resolvedSoundEvent = Resolvable.resolve(context, this.soundEvent);

        if (namespacedKey == null || resolvedDuration == null || resolvedRange == null || resolvedDescription == null || resolvedSoundEvent == null) {
            return null;
        }

        return new ZMusicInstrument(namespacedKey, resolvedDuration, resolvedRange, resolvedDescription, resolvedSoundEvent);
    }
}
