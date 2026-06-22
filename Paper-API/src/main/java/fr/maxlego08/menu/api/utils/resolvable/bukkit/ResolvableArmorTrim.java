package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableArmorTrim implements Resolvable<ArmorTrim> {

    private final @NotNull ResolvableRegistryEntry<TrimMaterial> material;
    private final @NotNull ResolvableRegistryEntry<TrimPattern> pattern;

    public ResolvableArmorTrim(@NotNull ResolvableRegistryEntry<TrimMaterial> material, @NotNull ResolvableRegistryEntry<TrimPattern> pattern) {
        this.material = material;
        this.pattern = pattern;
    }

    public @NotNull ResolvableRegistryEntry<TrimMaterial> getMaterial() {
        return this.material;
    }

    public @NotNull ResolvableRegistryEntry<TrimPattern> getPattern() {
        return this.pattern;
    }

    @Override
    public @Nullable ArmorTrim resolve(@NotNull BuildContext context) {
        TrimMaterial resolvedMaterial = this.material.resolve(context);
        TrimPattern resolvedPattern = this.pattern.resolve(context);
        if (resolvedMaterial == null || resolvedPattern == null) {
            return null;
        }
        return new ArmorTrim(resolvedMaterial, resolvedPattern);
    }
}
