package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.zcore.utils.itemstack.ZPersistentDataType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class CustomDataComponent extends ItemComponent {

    private final @NotNull List<@NotNull ZPersistentDataType<?,?>> pdcEntries;

    public CustomDataComponent(@NotNull List<@NotNull ZPersistentDataType<?,?>> pdcEntries) {
        this.pdcEntries = pdcEntries;
    }

    public @NotNull List<@NotNull ZPersistentDataType<?,?>> getPdcEntries() {
        return this.pdcEntries;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
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
