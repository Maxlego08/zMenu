package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.PotionContentsComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionType;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.loader.components.AbstractEffectItemComponentLoader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotPotionContentsItemComponentLoader extends AbstractEffectItemComponentLoader {

    public SpigotPotionContentsItemComponentLoader(){
        super("potion-contents");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvablePotionType basePotionType = null;
        String potion = componentSection.getString("potion", "");
        if (!potion.isEmpty()) {
            if (potion.contains("%")) {
                basePotionType = ResolvablePotionType.of(potion);
            } else {
                try {
                    NamespacedKey potionKey = NamespacedKey.fromString(potion);
                    if (potionKey != null) {
                        basePotionType = ResolvablePotionType.of(Registry.POTION.getOrThrow(potionKey));
                    }
                } catch (Exception ignored) {
                }
            }
        }

        ResolvableColor color = null;
        Object customColor = componentSection.get("custom-color");
        if (customColor != null) {
            color = ResolvableColor.of(customColor);
        }

        List<ResolvablePotionEffect> customEffects = this.parseResolvablePotionEffects(componentSection.getMapList("custom-effects"));

        Resolvable<String> customName = null;
        String customNameStr = componentSection.getString("custom-name");
        if (customNameStr != null) {
            customName = customNameStr.contains("%") ? ResolvableString.ofExpression(customNameStr) : ResolvableString.of(customNameStr);
        }

        return new PotionContentsComponent(basePotionType, customName, color, customEffects);
    }


}
