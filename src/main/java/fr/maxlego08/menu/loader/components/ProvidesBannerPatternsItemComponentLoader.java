package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ProvidesBannerPatternsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.5")
public class ProvidesBannerPatternsItemComponentLoader extends ItemComponentLoader {

    public ProvidesBannerPatternsItemComponentLoader(){
        super("provides-banner-patterns");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;
        Key key = Key.key(value);
        TagKey<PatternType> patternTypeTagKey = RegistryKey.BANNER_PATTERN.tagKey(key);
        return new ProvidesBannerPatternsComponent(patternTypeTagKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        DataComponentType.Valued<Object> type = (DataComponentType.Valued<Object>) (DataComponentType.Valued<?>) DataComponentTypes.PROVIDES_BANNER_PATTERNS;
        Object value = itemStack.getData(type);
        TagKey<PatternType> tagKey;
        if (value instanceof TagKey<?> key) {
            tagKey = (TagKey<PatternType>) key;
        } else if (value instanceof Tag<?> tag) {
            tagKey = (TagKey<PatternType>) tag.tagKey();
        } else {
            return null;
        }
        return new ProvidesBannerPatternsComponent(RegistryKey.BANNER_PATTERN.tagKey(tagKey.key()));
    }
}
