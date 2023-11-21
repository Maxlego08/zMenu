package fr.maxlego08.menu.dupe;

import fr.maxlego08.menu.api.dupe.DupeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class PDCDupeManager implements DupeManager {

    private final NamespacedKey namespacedKey;

    public PDCDupeManager(Plugin plugin) {
        this.namespacedKey = new NamespacedKey(plugin, DupeManager.KEY);
    }

    @Override
    public ItemStack protectItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemStack;
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(namespacedKey, PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public boolean isDupeItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        return persistentDataContainer.has(this.namespacedKey, PersistentDataType.BOOLEAN);
    }
}
