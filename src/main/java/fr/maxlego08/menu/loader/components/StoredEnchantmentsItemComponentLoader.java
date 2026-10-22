package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.StoredEnchantmentsComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyStoredEnchantmentsComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEnchantment;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEnchantmentEntry;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
public class StoredEnchantmentsItemComponentLoader extends ItemComponentLoader {

    public StoredEnchantmentsItemComponentLoader(){
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

        if (entries.isEmpty()) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new StoredEnchantmentsComponent(entries)
                : new LegacyStoredEnchantmentsComponent(entries);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        ItemEnchantments enchantments = itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS);
        if (enchantments == null) return null;
        List<ResolvableEnchantmentEntry> entries = new ArrayList<>();
        enchantments.enchantments().forEach((enchantment, level) -> entries.add(new ResolvableEnchantmentEntry(ResolvableEnchantment.of(enchantment), ResolvableInt.of(level))));
        return new StoredEnchantmentsComponent(entries);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof EnchantmentStorageMeta storageMeta) || !storageMeta.hasStoredEnchants()) return null;
        List<ResolvableEnchantmentEntry> entries = new ArrayList<>();
        storageMeta.getStoredEnchants().forEach((enchantment, level) -> entries.add(new ResolvableEnchantmentEntry(ResolvableEnchantment.of(enchantment), ResolvableInt.of(level))));
        return new LegacyStoredEnchantmentsComponent(entries);
    }
}
