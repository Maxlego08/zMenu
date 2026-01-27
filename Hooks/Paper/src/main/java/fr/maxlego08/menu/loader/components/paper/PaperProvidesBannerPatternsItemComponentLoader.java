package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.paper.PaperProvidesBannerPatternsComponent;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PaperProvidesBannerPatternsItemComponentLoader extends ItemComponentLoader {

    public PaperProvidesBannerPatternsItemComponentLoader(){
        super("provides_banner_patterns");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;
        Key key = Key.key(value);
        TagKey<PatternType> patternTypeTagKey = RegistryKey.BANNER_PATTERN.tagKey(key);
        return new PaperProvidesBannerPatternsComponent(patternTypeTagKey);
    }
}
