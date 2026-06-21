package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.*;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Set;

public abstract class ResolvablePersistentDataEntry {

    protected final ResolvableNamespacedKey key;

    protected ResolvablePersistentDataEntry(@NotNull ResolvableNamespacedKey key) {
        this.key = key;
    }

    public abstract void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context);


    public static final class StringEntry extends ResolvablePersistentDataEntry {
        private final ResolvableString value;

        public StringEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableString value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            String resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.STRING, resolvedValue);
            }
        }
    }

    public static final class IntEntry extends ResolvablePersistentDataEntry {
        private final ResolvableInt value;

        public IntEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableInt value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            Integer resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.INTEGER, resolvedValue);
            }
        }
    }

    public static final class LongEntry extends ResolvablePersistentDataEntry {
        private final ResolvableLong value;

        public LongEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableLong value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            Long resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.LONG, resolvedValue);
            }
        }
    }

    public static final class DoubleEntry extends ResolvablePersistentDataEntry {
        private final ResolvableDouble value;

        public DoubleEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableDouble value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            Double resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.DOUBLE, resolvedValue);
            }
        }
    }

    public static final class FloatEntry extends ResolvablePersistentDataEntry {
        private final ResolvableFloat value;

        public FloatEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableFloat value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            Float resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.FLOAT, resolvedValue);
            }
        }
    }

    public static final class ByteEntry extends ResolvablePersistentDataEntry {
        private final ResolvableByte value;

        public ByteEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableByte value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            Byte resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.BYTE, resolvedValue);
            }
        }
    }

    public static final class BooleanEntry extends ResolvablePersistentDataEntry {
        private final ResolvableBoolean value;

        public BooleanEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableBoolean value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            Boolean resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.BOOLEAN, resolvedValue);
            }
        }
    }

    public static final class ShortEntry extends ResolvablePersistentDataEntry {
        private final ResolvableShort value;

        public ShortEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableShort value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            Short resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.SHORT, resolvedValue);
            }
        }
    }

    public static final class ByteArrayEntry extends ResolvablePersistentDataEntry {
        private final ResolvableByteArray value;

        public ByteArrayEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableByteArray value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            byte[] resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.BYTE_ARRAY, resolvedValue);
            }
        }
    }

    public static final class IntArrayEntry extends ResolvablePersistentDataEntry {
        private final ResolvableIntArray value;

        public IntArrayEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableIntArray value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            int[] resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.INTEGER_ARRAY, resolvedValue);
            }
        }
    }

    public static final class LongArrayEntry extends ResolvablePersistentDataEntry {
        private final ResolvableLongArray value;

        public LongArrayEntry(@NotNull ResolvableNamespacedKey key, @NotNull ResolvableLongArray value) {
            super(key);
            this.value = value;
        }

        @Override
        public void applyTo(@NotNull ItemMeta itemMeta, @NotNull BuildContext context) {
            NamespacedKey resolvedKey = this.key.resolve(context);
            long[] resolvedValue = this.value.resolve(context);
            if (resolvedKey != null && resolvedValue != null) {
                itemMeta.getPersistentDataContainer().set(resolvedKey, PersistentDataType.LONG_ARRAY, resolvedValue);
            }
        }
    }


    private static final Set<String> KNOWN_TYPES = Set.of(
            "string", "int", "integer", "long", "double", "float", "byte", "short", "boolean", "bool",
            "byte_array", "int_array", "long_array"
    );

    @Nullable
    public static ResolvablePersistentDataEntry fromKeyValue(@NotNull String rawKey, @NotNull Object rawValue) {
        ResolvableNamespacedKey key = ResolvableNamespacedKey.auto(rawKey);
        return fromValue(key, rawValue);
    }

    @Nullable
    public static ResolvablePersistentDataEntry fromValue(@NotNull ResolvableNamespacedKey key, @NotNull Object rawValue) {
        return switch (rawValue) {
            case String str -> fromStringValue(key, str);
            case Integer intValue -> new IntEntry(key, ResolvableInt.of(intValue));
            case Boolean boolValue -> new BooleanEntry(key, ResolvableBoolean.of(boolValue));
            case Double doubleValue -> new DoubleEntry(key, ResolvableDouble.of(doubleValue));
            case Float floatValue -> new FloatEntry(key, ResolvableFloat.of(floatValue));
            case Byte byteValue -> new ByteEntry(key, ResolvableByte.of(byteValue));
            case Short shortValue -> new ShortEntry(key, ResolvableShort.of(shortValue));
            case byte[] byteArray -> new ByteArrayEntry(key, ResolvableByteArray.of(byteArray));
            case int[] intArray -> new IntArrayEntry(key, ResolvableIntArray.of(intArray));
            case long[] longArray -> new LongArrayEntry(key, ResolvableLongArray.of(longArray));
            default -> null;
        };
    }

    @Nullable
    private static ResolvablePersistentDataEntry fromStringValue(@NotNull ResolvableNamespacedKey key, @NotNull String str) {
        int atIndex = str.lastIndexOf('@');
        if (atIndex > 0 && atIndex < str.length() - 1) {
            String type = str.substring(atIndex + 1).toLowerCase(Locale.ROOT);
            String rawValue = str.substring(0, atIndex);
            if (KNOWN_TYPES.contains(type)) {
                return switch (type) {
                    case "string" -> new StringEntry(key, ResolvableString.auto(rawValue));
                    case "int", "integer" -> {
                        ResolvableInt resolved = ResolvableInt.autoOrNull(rawValue);
                        yield new IntEntry(key, resolved);
                    }
                    case "long" -> {
                        ResolvableLong resolved = ResolvableLong.autoOrNull(rawValue);
                        yield new LongEntry(key, resolved);
                    }
                    case "double" -> {
                        ResolvableDouble resolved = ResolvableDouble.autoOrNull(rawValue);
                        yield new DoubleEntry(key, resolved);
                    }
                    case "float" -> {
                        ResolvableFloat resolved = ResolvableFloat.autoOrNull(rawValue);
                        yield new FloatEntry(key, resolved);
                    }
                    case "byte" -> {
                        ResolvableByte resolved = ResolvableByte.autoOrNull(rawValue);
                        yield new ByteEntry(key, resolved);
                    }
                    case "short" -> {
                        ResolvableShort resolved = ResolvableShort.autoOrNull(rawValue);
                        yield new ShortEntry(key, resolved);
                    }
                    case "boolean", "bool" -> {
                        ResolvableBoolean resolved = ResolvableBoolean.autoOrNull(rawValue);
                        yield new BooleanEntry(key, resolved);
                    }
                    case "byte_array" -> new ByteArrayEntry(key, ResolvableByteArray.auto(rawValue));
                    case "int_array" -> new IntArrayEntry(key, ResolvableIntArray.auto(rawValue));
                    case "long_array" -> new LongArrayEntry(key, ResolvableLongArray.auto(rawValue));
                    default -> null;
                };
            }
        }
        return new StringEntry(key, ResolvableString.auto(str));
    }
}
