package fr.maxlego08.menu.api.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemUtil {
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
