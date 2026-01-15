package fr.maxlego08.menu.itemstack.paper.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.zcore.utils.PaperItemUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CustomNameComponent(
    @NotNull String customName
) implements ItemComponent {
    @Override
    public void apply(@NotNull org.bukkit.inventory.ItemStack itemStack, @Nullable Player player) {
        PaperItemUtils.setCustomName(itemStack, this.customName);
    }
}
