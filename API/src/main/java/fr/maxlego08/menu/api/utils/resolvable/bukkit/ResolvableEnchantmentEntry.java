package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ResolvableEnchantmentEntry implements Resolvable<AbstractMap.SimpleEntry<Enchantment, Integer>> {
    private final ResolvableEnchantment enchantment;
    private final ResolvableInt level;

    public ResolvableEnchantmentEntry(@NotNull ResolvableEnchantment enchantment, @NotNull ResolvableInt level) {
        super();
        this.enchantment = enchantment;
        this.level = level;
    }

    @Override
    public @Nullable AbstractMap.SimpleEntry<Enchantment, Integer> resolve(@NotNull BuildContext context) {
        Enchantment ench = this.enchantment.resolve(context);
        Integer lvl = this.level.resolve(context);
        if (ench == null || lvl == null) return null;
        return new AbstractMap.SimpleEntry<>(ench, lvl);
    }


    @Override
    public @NotNull Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(String.valueOf(this.enchantment.serialize()), this.level.serialize());
        return map;
    }
}
