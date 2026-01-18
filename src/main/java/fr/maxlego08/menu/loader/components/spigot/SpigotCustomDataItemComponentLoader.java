package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.CustomDataComponent;
import fr.maxlego08.menu.zcore.utils.itemstack.ZPersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpigotCustomDataItemComponentLoader extends ItemComponentLoader {

    public SpigotCustomDataItemComponentLoader() {
        super("custom_data");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        List<ZPersistentDataType<?, ?>> pdcEntries = new ArrayList<>();

        for (String key : componentSection.getKeys(false)) {
            Object value = componentSection.get(key);
            if (value == null) continue;

            NamespacedKey namespacedKey = NamespacedKey.fromString(key);
            if (namespacedKey == null) {
                continue;
            }

            Optional<ZPersistentDataType<?, ?>> pdcEntry = createPdcEntry(namespacedKey, value);
            pdcEntry.ifPresent(pdcEntries::add);
        }

        return pdcEntries.isEmpty() ? null : new CustomDataComponent(pdcEntries);
    }

    private Optional<ZPersistentDataType<?, ?>> createPdcEntry(NamespacedKey key, Object value) {
        if (value instanceof String stringValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.STRING, stringValue));
        }

        if (value instanceof Boolean boolValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.BOOLEAN, boolValue));
        }

        if (value instanceof Integer integerValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.INTEGER, integerValue));
        }

        if (value instanceof Long longValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.LONG, longValue));
        }

        if (value instanceof Double doubleValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.DOUBLE, doubleValue));
        }

        if (value instanceof Float floatValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.FLOAT, floatValue));
        }

        if (value instanceof Byte byteValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.BYTE, byteValue));
        }

        if (value instanceof Short shortValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.SHORT, shortValue));
        }

        if (value instanceof byte[] byteArrayValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.BYTE_ARRAY, byteArrayValue));
        }

        if (value instanceof int[] intArrayValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.INTEGER_ARRAY, intArrayValue));
        }

        if (value instanceof long[] longArrayValue) {
            return Optional.of(new ZPersistentDataType<>(key, PersistentDataType.LONG_ARRAY, longArrayValue));
        }

        return Optional.empty();
    }
}