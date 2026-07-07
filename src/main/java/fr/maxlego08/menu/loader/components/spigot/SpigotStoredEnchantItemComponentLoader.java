package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.StoredEnchantmentsComponent;
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
public class SpigotStoredEnchantItemComponentLoader extends ItemComponentLoader {

    public SpigotStoredEnchantItemComponentLoader(){
        super("stored-enchantments");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> values = componentSection.getValues(false);
        List<ResolvableEnchantmentEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String enchantName = entry.getKey();
            Object levelObj = entry.getValue();

            ResolvableEnchantment enchantment = ResolvableEnchantment.autoOrNull(enchantName);
            if (enchantment == null) continue;

            ResolvableInt level;
            if (levelObj instanceof Number number) {
                level = ResolvableInt.of(number.intValue());
            } else if (levelObj instanceof String expr) {
                level = ResolvableInt.of(expr);
            } else {
                continue;
            }

            entries.add(new ResolvableEnchantmentEntry(enchantment, level));
        }

        return entries.isEmpty() ? null : new StoredEnchantmentsComponent(entries);
    }
}
