package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.PotionContentsComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyPotionContentsComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableByte;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoComponentLoader
public class PotionContentsItemComponentLoader extends AbstractEffectItemComponentLoader {

    public PotionContentsItemComponentLoader(){
        super("potion-contents");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableRegistryEntry<PotionType> resolvableBasePotionType = ResolvableRegistry.autoOrNull(componentSection.getString("potion"), RegistryKey.POTION);

        ResolvableColor color = null;
        Object customColor = componentSection.get("custom-color");
        if (customColor != null) {
            color = ResolvableColor.of(customColor);
        }

        List<ResolvablePotionEffect> customEffects = this.parseResolvablePotionEffects(componentSection.getMapList("custom-effects"));

        Resolvable<String> customName = ResolvableString.autoOrNull(componentSection.getString("custom-name"));

        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new PotionContentsComponent(resolvableBasePotionType, customName, color, customEffects)
                : new LegacyPotionContentsComponent(resolvableBasePotionType, customName, color, customEffects);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        PotionContents potionContents = itemStack.getData(DataComponentTypes.POTION_CONTENTS);
        if (potionContents == null) return null;

        PotionType potion = potionContents.potion();
        Color customColor = potionContents.customColor();
        String customName = potionContents.customName();
        if (customName != null && Resolvable.isExpression(customName)) return null;

        List<ResolvablePotionEffect> customEffects = new ArrayList<>(potionContents.customEffects().size());
        for (PotionEffect effect : potionContents.customEffects()) {
            ResolvablePotionEffect resolvableEffect = toResolvablePotionEffect(effect);
            if (resolvableEffect == null) return null;
            customEffects.add(resolvableEffect);
        }

        return new PotionContentsComponent(
                potion == null ? null : ResolvableRegistry.ofValue(potion, RegistryKey.POTION),
                customName == null ? null : ResolvableString.of(customName),
                customColor == null ? null : ResolvableColor.of(customColor),
                customEffects
        );
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof PotionMeta potionMeta)) return null;
        if (!potionMeta.hasBasePotionType() && !potionMeta.hasColor() && !potionMeta.hasCustomEffects()) return null;

        PotionType potion = potionMeta.hasBasePotionType() ? potionMeta.getBasePotionType() : null;
        Color customColor = potionMeta.hasColor() ? potionMeta.getColor() : null;

        List<ResolvablePotionEffect> customEffects = new ArrayList<>();
        for (PotionEffect effect : potionMeta.getCustomEffects()) {
            ResolvablePotionEffect resolvableEffect = toResolvablePotionEffect(effect);
            if (resolvableEffect == null) return null;
            customEffects.add(resolvableEffect);
        }

        return new LegacyPotionContentsComponent(
                potion == null ? null : ResolvableRegistry.ofValue(potion, RegistryKey.POTION),
                null,
                customColor == null ? null : ResolvableColor.of(customColor),
                customEffects
        );
    }

    private static @Nullable ResolvablePotionEffect toResolvablePotionEffect(@NotNull PotionEffect effect) {
        if (effect.getAmplifier() != 0 && effect.getAmplifier() != 1) return null;
        if (effect.getHiddenPotionEffect() != null) return null;
        return new ResolvablePotionEffect(
                ResolvableString.of(effect.getType().getKey().toString()),
                ResolvableInt.of(effect.getDuration()),
                ResolvableByte.of((byte) effect.getAmplifier()),
                ResolvableBoolean.of(effect.isAmbient()),
                ResolvableBoolean.of(effect.hasParticles()),
                ResolvableBoolean.of(effect.hasIcon())
        );
    }

}
