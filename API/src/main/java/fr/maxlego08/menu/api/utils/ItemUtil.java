package fr.maxlego08.menu.api.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Utility class for operations related to ItemStack metadata.
 * Provides methods to safely edit item meta with type safety using consumers.
 */
public class ItemUtil {
    /**
     * Edits the meta of a given ItemStack if its metadata can be cast to the provided metaClass.
     * If successful, the provided consumer is used to perform modifications on the meta, and then sets it back to the item.
     *
     * @param item      The item whose meta should be edited.
     * @param metaClass The class object of the desired ItemMeta type (e.g., SkullMeta.class).
     * @param consumer  The consumer that applies modifications to the meta.
     * @param <T>       The specific ItemMeta type.
     * @return true if the meta was edited and set, false otherwise.
     */
    public static <T extends ItemMeta> boolean editMeta(@NotNull ItemStack item,@NotNull Class<T> metaClass,@NotNull Consumer<T> consumer) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        if (metaClass.isInstance(meta)) {
            T metaItem = metaClass.cast(meta);
            consumer.accept(metaItem);
            item.setItemMeta(metaItem);
            return true;
        }
        return false;
    }
}
