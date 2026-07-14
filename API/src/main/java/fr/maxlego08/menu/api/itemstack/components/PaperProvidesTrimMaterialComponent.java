package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperProvidesTrimMaterialComponent extends ItemComponent {
    private final ResolvableRegistryEntry<TrimMaterial> trimMaterial;

    public PaperProvidesTrimMaterialComponent(@Nullable ResolvableRegistryEntry<TrimMaterial> trimMaterial) {
        this.trimMaterial = trimMaterial;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.trimMaterial, trimMaterial -> itemStack.setData(DataComponentTypes.PROVIDES_TRIM_MATERIAL, trimMaterial));
    }
}
