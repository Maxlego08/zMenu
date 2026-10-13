package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.SuspiciousStewEffectsComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacySuspiciousStewEffectsComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableByte;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
public class SuspiciousStewEffectsItemComponentLoader extends AbstractEffectItemComponentLoader {

    public SuspiciousStewEffectsItemComponentLoader() {
        super("suspicious-stew-effects");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        List<Map<?, ?>> effects = configuration.getMapList(path);
        List<ResolvablePotionEffect> resolvablePotionEffects = this.parseResolvablePotionEffects(effects);
        if (resolvablePotionEffects.isEmpty()) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new SuspiciousStewEffectsComponent(resolvablePotionEffects)
                : new LegacySuspiciousStewEffectsComponent(resolvablePotionEffects);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        SuspiciousStewEffects stewEffects = itemStack.getData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
        if (stewEffects == null) return null;
        if (stewEffects.effects().isEmpty()) return null;

        List<ResolvablePotionEffect> effects = new ArrayList<>(stewEffects.effects().size());
        for (SuspiciousEffectEntry entry : stewEffects.effects()) {
            effects.add(new ResolvablePotionEffect(
                    ResolvableString.of(entry.effect().getKey().toString()),
                    ResolvableInt.of(entry.duration()),
                    ResolvableByte.of((byte) 0),
                    ResolvableBoolean.of(false),
                    ResolvableBoolean.of(true),
                    ResolvableBoolean.of(true)
            ));
        }
        return new SuspiciousStewEffectsComponent(effects);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof SuspiciousStewMeta stewMeta) || !stewMeta.hasCustomEffects()) return null;

        List<ResolvablePotionEffect> effects = new ArrayList<>();
        for (PotionEffect effect : stewMeta.getCustomEffects()) {
            effects.add(new ResolvablePotionEffect(
                    ResolvableString.of(effect.getType().getKey().toString()),
                    ResolvableInt.of(effect.getDuration()),
                    ResolvableByte.of((byte) 0),
                    ResolvableBoolean.of(false),
                    ResolvableBoolean.of(true),
                    ResolvableBoolean.of(true)
            ));
        }
        return new LegacySuspiciousStewEffectsComponent(effects);
    }
}
