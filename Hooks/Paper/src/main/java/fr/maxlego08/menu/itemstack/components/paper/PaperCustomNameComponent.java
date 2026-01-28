package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.placeholder.Placeholder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperCustomNameComponent extends ItemComponent {
    private final String customName;
    private final PaperMetaUpdater paperMetaUpdater;

    public PaperCustomNameComponent(String customName, PaperMetaUpdater paperMetaUpdater) {
        this.customName = customName;
        this.paperMetaUpdater = paperMetaUpdater;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.CUSTOM_NAME, this.paperMetaUpdater.getComponent(context.getPlaceholders().parse(Placeholder.Placeholders.getPlaceholder().setPlaceholders(context.getPlayer(), customName))));
    }
}
