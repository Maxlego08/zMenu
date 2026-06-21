package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class LoreComponent extends ItemComponent {

    private final List<@NotNull ResolvableString> lore;

    public LoreComponent(List<@NotNull ResolvableString> lore) {
        this.lore = lore;
    }

    public List<@NotNull ResolvableString> getLore() {
        return this.lore;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            Resolvable.applyResolvable(context, this.lore, itemMeta::setLore);

            itemStack.setItemMeta(itemMeta);
        }
    }

}
