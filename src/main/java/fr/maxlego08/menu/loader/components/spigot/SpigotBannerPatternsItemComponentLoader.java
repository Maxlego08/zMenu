package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BannerPatternsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableBannerPattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotBannerPatternsItemComponentLoader extends ItemComponentLoader {

    public SpigotBannerPatternsItemComponentLoader(){
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
        return resolvablePatterns.isEmpty() ? null : new BannerPatternsComponent(resolvablePatterns);
    }
}
