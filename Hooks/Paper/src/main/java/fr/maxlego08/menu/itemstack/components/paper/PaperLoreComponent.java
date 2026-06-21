package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PaperLoreComponent extends ItemComponent {
    private final List<ResolvableString> lore;
    private final PaperMetaUpdater paperMetaUpdater;


    public PaperLoreComponent(@NotNull List<@NotNull ResolvableString> lore, PaperMetaUpdater paperMetaUpdater) {
        this.lore = lore;
        this.paperMetaUpdater = paperMetaUpdater;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemLore.Builder builder = ItemLore.lore();
        for (ResolvableString loreLine : this.lore){
            String resolved = loreLine.resolve(context);
            if (resolved != null) {
                builder.addLine(this.paperMetaUpdater.getComponent(resolved));
            }
        }
        itemStack.setData(DataComponentTypes.LORE, builder.build());
    }
}
