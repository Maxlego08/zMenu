package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public record ResolvableArmorTrim(@NotNull ResolvableRegistryEntry<TrimMaterial> material,
                                  @NotNull ResolvableRegistryEntry<TrimPattern> pattern) implements Resolvable<ArmorTrim> {

    @Override
    public @Nullable ArmorTrim resolve(@NotNull BuildContext context) {
        TrimMaterial resolvedMaterial = this.material.resolve(context);
        TrimPattern resolvedPattern = this.pattern.resolve(context);
        if (resolvedMaterial == null || resolvedPattern == null) {
            return null;
        }
        return new ArmorTrim(resolvedMaterial, resolvedPattern);
    }

    /**
     * Writes {@code material} and {@code pattern} as registry keys, the section the trim loader reads.
     */
    @Override
    public @NotNull Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("material", this.material.serialize());
        map.put("pattern", this.pattern.serialize());
        return map;
    }
}
