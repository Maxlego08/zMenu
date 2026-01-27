package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BannerPatternsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpigotBannerPatternsItemComponentLoader extends ItemComponentLoader {

    public SpigotBannerPatternsItemComponentLoader(){
        super("banner-patterns");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        List<Map<?, ?>> rawPatterns = configuration.getMapList(path);
        List<@NotNull Pattern> patterns = new ArrayList<>();
        for (var rawPattern : rawPatterns) {
            @SuppressWarnings("unchecked")
            Map<String, Object> patternMap = (Map<String, Object>) rawPattern;
            try {
                patterns.add(new Pattern(patternMap));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return patterns.isEmpty() ? null : new BannerPatternsComponent(patterns);
    }
}
