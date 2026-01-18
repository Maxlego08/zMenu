package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CustomNameComponent(
        @NotNull String customName,
        @NotNull PaperMetaUpdater metaUpdater
        ) implements ItemComponent {
    @Override
    public void apply(@NotNull org.bukkit.inventory.ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.CUSTOM_NAME,this.metaUpdater.getComponent(this.customName));
    }
}
