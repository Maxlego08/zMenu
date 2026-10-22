package fr.maxlego08.menu.api.utils.itemstack;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

/**
 * @deprecated not used by zMenu.
 */
@Deprecated(since = "1.1.2.1", forRemoval = true)
public record ZPersistentDataType<C,T>(NamespacedKey namespacedKey, PersistentDataType<C,T> persistentDataType, T value) {

}
