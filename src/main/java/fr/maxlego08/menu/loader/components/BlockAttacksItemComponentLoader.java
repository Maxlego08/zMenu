package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BlockAttacksComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.paper.*;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.damage.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.21.5")
public final class BlockAttacksItemComponentLoader extends ItemComponentLoader {

    public BlockAttacksItemComponentLoader() {
        super("blocks-attacks");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableFloat blockDelaySeconds = this.asResolvableFloat(componentSection, "block-delay-seconds");
        ResolvableFloat disableCooldownScale = this.asResolvableFloat(componentSection, "disable-cooldown-scale");
        ResolvableNamespacedKey blockSound = this.asResolvableKey(componentSection, "block-sound");
        ResolvableNamespacedKey disableSound = this.asResolvableKey(componentSection, "disable-sound");
        TagKeyResolvable<DamageType> bypassedBy = ResolvableRegistryKey.tagKeyOrNull(RegistryKey.DAMAGE_TYPE, componentSection.getString("bypassed_by"));

        ResolvableItemDamageFunction itemDamage = null;
        ConfigurationSection itemDamageSection = componentSection.getConfigurationSection("item_damage");
        if (itemDamageSection != null) {
            ResolvableFloat threshold = this.asResolvableFloat(itemDamageSection, "threshold");
            ResolvableFloat base = this.asResolvableFloat(itemDamageSection, "base");
            ResolvableFloat factor = this.asResolvableFloat(itemDamageSection, "factor");
            itemDamage = new ResolvableItemDamageFunction(threshold, base, factor);
        }

        List<Map<?, ?>> damageReductions = componentSection.getMapList("damage_reductions");
        List<ResolvableDamageReduction> damageReductionList = null;
        for (Map<?, ?> damageReductionMap : damageReductions) {
            if (damageReductionMap == null) continue;
            ConfigurationSection damageReductionConfiguration = new YamlConfiguration();
            ConfigurationSection damageReduction = damageReductionConfiguration.createSection("damage_reduction", damageReductionMap);
            ResolvableFloat horizontalBlockingAngle = this.asResolvableFloat(damageReduction, "horizontal_blocking_angle");
            ResolvableFloat base = this.asResolvableFloat(damageReduction, "base");
            ResolvableFloat factor = this.asResolvableFloat(damageReduction, "factor");
            Resolvable<RegistryKeySet<DamageType>> type = ResolvableRegistryKeySet.typedKeySetOrNull(RegistryKey.DAMAGE_TYPE, damageReduction.get("type"));
            if (type != null) {
                ResolvableDamageReduction damageReductionResolvable = new ResolvableDamageReduction(type, horizontalBlockingAngle, base, factor);
                if (damageReductionList == null) {
                    damageReductionList = new java.util.ArrayList<>();
                }
                damageReductionList.add(damageReductionResolvable);
            }
        }


        return new BlockAttacksComponent(
                blockDelaySeconds,
                disableCooldownScale,
                blockSound,
                disableSound,
                bypassedBy,
                itemDamage,
                damageReductionList
        );
    }
}
