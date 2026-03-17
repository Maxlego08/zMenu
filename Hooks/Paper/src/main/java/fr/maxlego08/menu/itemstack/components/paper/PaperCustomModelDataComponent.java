package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperCustomModelDataComponent extends ItemComponent {
    private final CustomModelData customModelData;

    public PaperCustomModelDataComponent(@NotNull CustomModelData customModelData) {
        this.customModelData = customModelData;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {

        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, this.customModelData);
    }
}
