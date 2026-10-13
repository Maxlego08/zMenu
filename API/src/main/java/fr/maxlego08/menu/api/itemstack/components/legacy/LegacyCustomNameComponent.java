package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyCustomNameComponent extends ItemComponent {
    private final ResolvableString customName;

    public LegacyCustomNameComponent(ResolvableString customName) {
        this.customName = customName;
    }

    public ResolvableString getCustomName() {
        return this.customName;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        this.applyResolvable(context, itemMeta::setDisplayName, this.customName);

        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public @Nullable Object serialize() {
        return this.customName.serialize();
    }
}
