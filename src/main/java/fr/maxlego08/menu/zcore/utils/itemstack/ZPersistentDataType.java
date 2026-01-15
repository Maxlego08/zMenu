package fr.maxlego08.menu.zcore.utils.itemstack;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public record ZPersistentDataType<C,T>(NamespacedKey namespacedKey, PersistentDataType<C,T> persistentDataType, T value) {

}
