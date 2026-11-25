package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.ResolvablePersistentDataEntry;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.CustomDataComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyCustomDataComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.NamespacedKey;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class CustomDataItemComponentLoader extends ItemComponentLoader {

    public CustomDataItemComponentLoader() {
        super("custom-data");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;

        List<ResolvablePersistentDataEntry> pdcEntries = new ArrayList<>();

        for (String key : componentSection.getRoutesAsStrings(false)) {
            Object value = componentSection.get(key);
            if (value == null) continue;

            ResolvablePersistentDataEntry entry = ResolvablePersistentDataEntry.fromKeyValue(key, value);
            if (entry != null) {
                pdcEntries.add(entry);
            }
        }

        if (pdcEntries.isEmpty()) return null;
        return MinecraftVersion.isServerAtLeast("1.21.4")
                ? new CustomDataComponent(pdcEntries)
                : new LegacyCustomDataComponent(pdcEntries);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        if (!MinecraftVersion.isServerAtLeast("1.21.4")) return null;
        PersistentDataContainerView container = itemStack.getPersistentDataContainer();
        if (container.isEmpty()) return null;

        List<ResolvablePersistentDataEntry> entries = new ArrayList<>();
        for (NamespacedKey key : container.getKeys()) {
            ResolvablePersistentDataEntry entry = ResolvablePersistentDataEntry.fromContainer(container, key);
            if (entry == null) return null;
            entries.add(entry);
        }
        return new CustomDataComponent(entries);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.isEmpty()) return null;

        List<ResolvablePersistentDataEntry> entries = new ArrayList<>();
        for (NamespacedKey key : container.getKeys()) {
            ResolvablePersistentDataEntry entry = ResolvablePersistentDataEntry.fromContainer(container, key);
            if (entry == null) return null;
            entries.add(entry);
        }
        return new LegacyCustomDataComponent(entries);
    }
}
