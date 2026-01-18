package fr.maxlego08.menu.loader.components;

import fr.maxlego08.common.loader.components.AbstractEffectItemComponentLoader;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.PotionContentsComponent;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class SpigotPotionContentsItemComponentLoader extends AbstractEffectItemComponentLoader {

    public SpigotPotionContentsItemComponentLoader(){
        super("potion_contents");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        @Nullable PotionType basePotionType = null;
        String potion = componentSection.getString("potion", "");
        if (!potion.isEmpty()) {
            try {
                NamespacedKey potionKey = NamespacedKey.fromString(potion);
                if (potionKey != null) {
                    basePotionType = Registry.POTION.getOrThrow(potionKey);
                }
            } catch (Exception ignored) {
            }
        }

        @Nullable Color color = null;
        Object customColor = componentSection.get("custom_color");
        if (customColor != null) {
            color = this.parseColor(customColor);
        }

        List<PotionEffect> customEffects = parsePotionEffects(componentSection.getMapList("custom_effects"));

        String customName = componentSection.getString("custom_name", null);
        return new PotionContentsComponent(basePotionType, customName, color, customEffects);
    }
}
