package fr.maxlego08.menu.api;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * A class that provides utility methods for creating namespaced keys with zmenu as the namespace.
 * This class cannot be instantiated.
 */
public final class ZMenuKeys {
    public static final String NAMESPACE = "zmenu";

    /**
     * The constructor is private to prevent instantiation.
     */
    private ZMenuKeys() {
    }

    /**
     * Creates a namespaced key with the namespace {@link #NAMESPACE} and the given value.
     *
     * @param value the value of the key
     * @return a namespaced key with the namespace {@link #NAMESPACE} and the given value
     */
    public static @NotNull Key key(@NotNull String value) {
        return Key.key(NAMESPACE, value);
    }

    /**
     * Gets the key of something read from a file, such as an inventory. Unlike
     * {@link #parse(String)}, "-" stays "-", so {@code my-shop} and {@code my_shop} stay apart.
     *
     * @param namespace usually the name of the plugin that owns the file
     * @param fileName  the name of the file, without its extension
     * @return the key
     */
    public static @NotNull Key fileKey(@NotNull String namespace, @NotNull String fileName) {
        return Key.key(sanitize(namespace), sanitize(fileName));
    }

    /**
     * Reads a reference to an inventory written in a file or a command: {@code plugin:file} or
     * {@code file}, which belongs to {@link #NAMESPACE}.
     *
     * @param reference the reference
     * @return the key of the inventory
     */
    public static @NotNull Key inventoryKey(@NotNull String reference) {
        int separator = reference.indexOf(':');
        return separator < 0 ? fileKey(NAMESPACE, reference) : fileKey(reference.substring(0, separator), reference.substring(separator + 1));
    }

    /**
     * Tells whether a reference names this key. {@code plugin:file} must match exactly, while a
     * plain {@code file} matches that file name in any namespace, as name lookups always did.
     *
     * @param key       the key of an inventory
     * @param reference the reference
     * @return whether they match
     */
    public static boolean matches(@NotNull Key key, @NotNull String reference) {
        Key referenced = inventoryKey(reference);
        return reference.indexOf(':') < 0 ? key.value().equals(referenced.value()) : key.equals(referenced);
    }

    /**
     * Lowercases a name and replaces every character a key cannot hold with "_", so that a file
     * named {@code My Shop} is {@code my_shop}.
     */
    private static String sanitize(String name) {
        String lower = name.trim().toLowerCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder(lower.length());
        for (int index = 0; index < lower.length(); index++) {
            char character = lower.charAt(index);
            boolean valid = (character >= 'a' && character <= 'z') || (character >= '0' && character <= '9') || character == '_' || character == '-' || character == '.';
            builder.append(valid ? character : '_');
        }
        return builder.isEmpty() ? "_" : builder.toString();
    }

    /**
     * Reads a key written in a file or a command. The case is ignored and "-" is read as "_".
     * Without a namespace, the key belongs to {@link #NAMESPACE}: {@code spiral} is
     * {@code zmenu:spiral}, while {@code myplugin:spiral} keeps its own namespace.
     *
     * @param input the text to read
     * @return the key
     * @throws net.kyori.adventure.key.InvalidKeyException if the text is not a valid key
     */
    public static @NotNull Key parse(@NotNull String input) {
        String text = input.trim().toLowerCase(Locale.ROOT);
        int separator = text.indexOf(':');
        String namespace = separator < 0 ? NAMESPACE : text.substring(0, separator);
        String value = text.substring(separator + 1).replace('-', '_');
        return Key.key(namespace, value);
    }
}
