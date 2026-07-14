package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.ResolvablePersistentDataEntry;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class CustomDataComponent extends ItemComponent {

    private final @NotNull List<@NotNull ResolvablePersistentDataEntry> pdcEntries;

    public CustomDataComponent(@NotNull List<@NotNull ResolvablePersistentDataEntry> pdcEntries) {
        this.pdcEntries = pdcEntries;
    }

    public @NotNull List<@NotNull ResolvablePersistentDataEntry> getPdcEntries() {
        return this.pdcEntries;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            for (ResolvablePersistentDataEntry entry : this.pdcEntries) {
                entry.applyTo(itemMeta, context);
            }
            itemStack.setItemMeta(itemMeta);
        }
    }

}
