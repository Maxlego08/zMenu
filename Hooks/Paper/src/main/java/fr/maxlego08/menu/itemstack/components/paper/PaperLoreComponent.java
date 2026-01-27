package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.placeholder.Placeholder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PaperLoreComponent extends ItemComponent {
    private final List<String> lore;
    private final PaperMetaUpdater paperMetaUpdater;
    private final Placeholder placeholder;


    public PaperLoreComponent(@NotNull List<@NotNull String> lore, PaperMetaUpdater paperMetaUpdater) {
        this.lore = lore;
        this.paperMetaUpdater = paperMetaUpdater;
        this.placeholder = Placeholder.Placeholders.getPlaceholder();
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemLore.Builder builder = ItemLore.lore();
        for (String loreLine : this.lore){
            builder.addLine(this.paperMetaUpdater.getComponent(placeholder.setPlaceholders(context.getPlayer(), context.getPlaceholders().parse(loreLine))));
        }
        itemStack.setData(DataComponentTypes.LORE, builder.build());
    }
}
