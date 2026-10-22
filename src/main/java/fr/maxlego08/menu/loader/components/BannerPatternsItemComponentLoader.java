package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BannerPatternsComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyBannerPatternsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableBannerPattern;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BannerPatternLayers;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
public class BannerPatternsItemComponentLoader extends ItemComponentLoader {

    public BannerPatternsItemComponentLoader(){
        super("banner-patterns");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        List<Map<?, ?>> rawPatterns = configuration.getMapList(path);
        List<ResolvableBannerPattern> resolvablePatterns = new ArrayList<>();
        for (var rawPattern : rawPatterns) {
            @SuppressWarnings("unchecked")
            Map<String, Object> patternMap = (Map<String, Object>) rawPattern;
            ResolvableBannerPattern resolvable = ResolvableBannerPattern.fromMap(patternMap);
            if (resolvable != null) {
                resolvablePatterns.add(resolvable);
            }
        }
        if (resolvablePatterns.isEmpty()) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new BannerPatternsComponent(resolvablePatterns)
                : new LegacyBannerPatternsComponent(resolvablePatterns);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        BannerPatternLayers layers = itemStack.getData(DataComponentTypes.BANNER_PATTERNS);
        if (layers == null || layers.patterns().isEmpty()) return null;

        List<ResolvableBannerPattern> patterns = new ArrayList<>(layers.patterns().size());
        for (Pattern pattern : layers.patterns()) {
            ResolvableRegistryEntry<PatternType> patternType = ResolvableRegistry.ofRegisteredOrNull(pattern.getPattern(), RegistryKey.BANNER_PATTERN);
            if (patternType == null) return null;
            patterns.add(new ResolvableBannerPattern(ResolvableEnum.of(DyeColor.class, pattern.getColor()), patternType));
        }
        return new BannerPatternsComponent(patterns);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        List<ResolvableBannerPattern> patterns = readPatterns(itemStack.getItemMeta());
        return patterns == null ? null : new LegacyBannerPatternsComponent(patterns);
    }

    private static @Nullable List<ResolvableBannerPattern> readPatterns(@Nullable ItemMeta itemMeta) {
        if (!(itemMeta instanceof BannerMeta bannerMeta) || bannerMeta.getPatterns().isEmpty()) return null;

        List<ResolvableBannerPattern> patterns = new ArrayList<>(bannerMeta.getPatterns().size());
        for (Pattern pattern : bannerMeta.getPatterns()) {
            ResolvableRegistryEntry<PatternType> patternType = ResolvableRegistry.ofRegisteredOrNull(pattern.getPattern(), RegistryKey.BANNER_PATTERN);
            if (patternType == null) return null;
            patterns.add(new ResolvableBannerPattern(ResolvableEnum.of(DyeColor.class, pattern.getColor()), patternType));
        }
        return patterns;
    }
}
