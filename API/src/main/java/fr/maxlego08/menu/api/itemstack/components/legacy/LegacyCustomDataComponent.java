package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.ResolvablePersistentDataEntry;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.CustomDataComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LegacyCustomDataComponent extends CustomDataComponent {

    public LegacyCustomDataComponent(@NotNull List<@NotNull ResolvablePersistentDataEntry> pdcEntries) {
        super(pdcEntries);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        for (ResolvablePersistentDataEntry entry : this.getPdcEntries()) {
            entry.applyTo(itemMeta.getPersistentDataContainer(), context);
        }

        itemStack.setItemMeta(itemMeta);
    }
}
