package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record PaperLoreComponent(
    @NotNull List<@NotNull String> lore,
    @NotNull PaperMetaUpdater metaUpdater
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        ItemLore.Builder builder = ItemLore.lore();
        for (String loreLine : this.lore){
            builder.addLine(this.metaUpdater.getComponent(loreLine));
        }
        itemStack.setData(DataComponentTypes.LORE, builder.build());
    }
}
