package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.EnchantementsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEnchantment;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEnchantmentEntry;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
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
public class SpigotEnchantmentsItemComponentLoader extends ItemComponentLoader {

    public SpigotEnchantmentsItemComponentLoader(){
        super("enchantments");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> values = componentSection.getValues(false);
        List<ResolvableEnchantmentEntry> enchantments = new ArrayList<>();

        for (var entry : values.entrySet()) {
            String enchantmentName = entry.getKey();

            ResolvableEnchantment resolvableEnchantment = ResolvableEnchantment.autoOrNull(enchantmentName);
            if (resolvableEnchantment == null) continue;

            int levelValue = componentSection.getInt(enchantmentName, -1);
            if (levelValue < 1 && !(entry.getValue() instanceof String)) continue;

            ResolvableInt resolvableLevel;
            if (entry.getValue() instanceof String levelString && levelString.contains("%")) {
                resolvableLevel = ResolvableInt.of(levelString);
            } else if (levelValue >= 1) {
                resolvableLevel = ResolvableInt.of(levelValue);
            } else {
                continue;
            }

            enchantments.add(new ResolvableEnchantmentEntry(resolvableEnchantment, resolvableLevel));
        }

        return enchantments.isEmpty() ? null : new EnchantementsComponent(enchantments);
    }
}
