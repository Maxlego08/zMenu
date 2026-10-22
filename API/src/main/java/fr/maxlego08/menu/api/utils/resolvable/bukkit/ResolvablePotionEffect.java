package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableByte;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ResolvablePotionEffect implements Resolvable<PotionEffect> {

    private final Resolvable<String> typeId;
    private final ResolvableInt duration;
    private final ResolvableByte amplifier;
    private final ResolvableBoolean ambient;
    private final ResolvableBoolean particles;
    private final ResolvableBoolean showIcon;

    public ResolvablePotionEffect(@NotNull Resolvable<String> typeId, @NotNull ResolvableInt duration, @NotNull ResolvableByte amplifier, @NotNull ResolvableBoolean ambient, @NotNull ResolvableBoolean particles, @NotNull ResolvableBoolean showIcon) {
        this.typeId = typeId;
        this.duration = duration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.particles = particles;
        this.showIcon = showIcon;
    }

    @Override
    public @Nullable PotionEffect resolve(@NotNull BuildContext context) {
        String resolvedId = this.typeId.resolve(context);
        if (resolvedId == null) return null;

        NamespacedKey key = NamespacedKey.fromString(resolvedId);
        if (key == null) return null;

        PotionEffectType type = Registry.EFFECT.getOrThrow(key);

        Integer intDuration = this.duration.resolve(context);
        Byte byteAmplifier = this.amplifier.resolve(context);
        Boolean boolAmbient = this.ambient.resolve(context);
        Boolean boolParticles = this.particles.resolve(context);
        Boolean boolShowIcon = this.showIcon.resolve(context);

        if (intDuration == null || byteAmplifier == null || boolAmbient == null || boolParticles == null || boolShowIcon == null) {
            return null;
        }
        return new PotionEffect(type, intDuration, byteAmplifier, boolAmbient, boolParticles, boolShowIcon);
    }

    /**
     * Writes the effect map the effect loaders read: {@code id}, {@code duration}, {@code amplified} (amplifier 1 as
     * {@code true}, 0 as {@code false}, the expression when dynamic), {@code ambient}, {@code show_particles} and {@code show_icon}.
     *
     * @throws UnsupportedOperationException If the amplifier is fixed to anything but 0 or 1, which the loaders cannot read back.
     */
    @Override
    public @NotNull Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", this.typeId.serialize());
        map.put("duration", this.duration.serialize());
        Byte fixedAmplifier = this.amplifier.getResolvedValue();
        if (fixedAmplifier != null && fixedAmplifier != 0 && fixedAmplifier != 1) {
            throw new UnsupportedOperationException("Potion effect amplifier " + fixedAmplifier + " cannot be written, only 0 or 1 (amplified) can");
        }
        map.put("amplified", fixedAmplifier != null ? fixedAmplifier == 1 : this.amplifier.serialize());
        map.put("ambient", this.ambient.serialize());
        map.put("show-particles", this.particles.serialize());
        map.put("show-icon", this.showIcon.serialize());
        return map;
    }
}
