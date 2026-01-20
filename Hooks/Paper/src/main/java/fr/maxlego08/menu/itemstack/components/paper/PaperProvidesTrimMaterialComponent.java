package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperProvidesTrimMaterialComponent extends ItemComponent {
    private final @NotNull TrimMaterial trimMaterial;

    public PaperProvidesTrimMaterialComponent(@NotNull TrimMaterial trimMaterial) {
        this.trimMaterial = trimMaterial;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.PROVIDES_TRIM_MATERIAL, trimMaterial);
    }
}
