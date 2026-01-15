package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.zcore.utils.itemstack.ZPersistentDataType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record CustomDataComponent(
    @NotNull List<@NotNull ZPersistentDataType<?,?>> pdcEntries
) implements ItemComponent {

    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            for (ZPersistentDataType<?,?> entry : this.pdcEntries) {
                applyPdcEntry(itemMeta, entry);
            }
            itemStack.setItemMeta(itemMeta);
        }
    }

    private <C, T> void applyPdcEntry(ItemMeta itemMeta, ZPersistentDataType<C, T> entry) {
        itemMeta.getPersistentDataContainer().set(
            entry.namespacedKey(),
            entry.persistentDataType(),
            entry.value()
        );
    }
}