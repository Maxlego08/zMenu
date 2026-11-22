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
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

@AutoComponentLoader
@SinceVersion("1.21.5")
public final class BlockAttacksItemComponentLoader extends ItemComponentLoader {

    public BlockAttacksItemComponentLoader() {
        super("blocks-attacks");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;

        ResolvableFloat blockDelaySeconds = this.asResolvableFloat(componentSection, "block-delay-seconds");
        ResolvableFloat disableCooldownScale = this.asResolvableFloat(componentSection, "disable-cooldown-scale");
        ResolvableNamespacedKey blockSound = this.asResolvableKey(componentSection, "block-sound");
        ResolvableNamespacedKey disableSound = this.asResolvableKey(componentSection, "disable-sound");
        TagKeyResolvable<DamageType> bypassedBy = fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableRegistryKey.tagKeyOrNull(RegistryKey.DAMAGE_TYPE, componentSection.getString("bypassed-by"));

        ResolvableItemDamageFunction itemDamage = null;
        Section itemDamageSection = componentSection.getSection("item-damage");
        if (itemDamageSection != null) {
            ResolvableFloat threshold = this.asResolvableFloat(itemDamageSection, "threshold");
            ResolvableFloat base = this.asResolvableFloat(itemDamageSection, "base");
            ResolvableFloat factor = this.asResolvableFloat(itemDamageSection, "factor");
            itemDamage = new ResolvableItemDamageFunction(threshold, base, factor);
        }

        List<Map<?, ?>> damageReductions = componentSection.getMapList("damage-reductions");
        List<ResolvableDamageReduction> damageReductionList = null;
        for (Map<?, ?> damageReductionMap : damageReductions) {
            if (damageReductionMap == null) continue;
            Section damageReductionConfiguration = YamlSections.empty();
            Section damageReduction = YamlSections.createSection(damageReductionConfiguration, "damage_reduction", damageReductionMap);
            ResolvableFloat horizontalBlockingAngle = this.asResolvableFloat(damageReduction, "horizontal-blocking-angle");
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

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        BlocksAttacks blocksAttacks = itemStack.getData(DataComponentTypes.BLOCKS_ATTACKS);
        if (blocksAttacks == null) return null;

        Object bypassedByValue;
        try {
            bypassedByValue = BlocksAttacks.class.getMethod("bypassedBy").invoke(blocksAttacks);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
        TagKeyResolvable<DamageType> bypassedBy = null;
        if (bypassedByValue instanceof TagKey<?> tagKey) {
            bypassedBy = ResolvableRegistryKey.tagKey(RegistryKey.DAMAGE_TYPE, tagKey.key().asString());
        } else if (bypassedByValue != null) {
            return null;
        }

        ItemDamageFunction itemDamageFunction = blocksAttacks.itemDamage();
        ResolvableItemDamageFunction itemDamage = new ResolvableItemDamageFunction(
                ResolvableFloat.of(itemDamageFunction.threshold()),
                ResolvableFloat.of(itemDamageFunction.base()),
                ResolvableFloat.of(itemDamageFunction.factor())
        );

        List<ResolvableDamageReduction> damageReductions = null;
        for (DamageReduction damageReduction : blocksAttacks.damageReductions()) {
            RegistryKeySet<DamageType> types = damageReduction.type();
            if (types == null || types instanceof Tag<DamageType>) return null;
            Resolvable<RegistryKeySet<DamageType>> type = ResolvableRegistryKeySet.of(types);
            if (type == null) return null;
            if (damageReductions == null) damageReductions = new ArrayList<>();
            damageReductions.add(new ResolvableDamageReduction(
                    type,
                    ResolvableFloat.of(damageReduction.horizontalBlockingAngle()),
                    ResolvableFloat.of(damageReduction.base()),
                    ResolvableFloat.of(damageReduction.factor())
            ));
        }

        return new BlockAttacksComponent(
                ResolvableFloat.of(blocksAttacks.blockDelaySeconds()),
                ResolvableFloat.of(blocksAttacks.disableCooldownScale()),
                this.toResolvableKey(blocksAttacks.blockSound()),
                this.toResolvableKey(blocksAttacks.disableSound()),
                bypassedBy,
                itemDamage,
                damageReductions
        );
    }

    private @Nullable ResolvableNamespacedKey toResolvableKey(@Nullable Key key) {
        if (key == null) return null;
        NamespacedKey namespacedKey = NamespacedKey.fromString(key.asString());
        return namespacedKey == null ? null : ResolvableNamespacedKey.of(namespacedKey);
    }
}
