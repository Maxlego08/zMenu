package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PaperLoreComponent extends ItemComponent {
    private final List<ResolvableComponent> lore;


    public PaperLoreComponent(@NotNull List<@NotNull ResolvableComponent> lore) {
        this.lore = lore;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemLore.Builder builder = ItemLore.lore();

        Resolvable.applyResolvable(context, this.lore, builder::addLines);

        itemStack.setData(DataComponentTypes.LORE, builder.build());
    }
}
