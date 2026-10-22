package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.CustomModelDataComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LegacyCustomModelDataComponent extends CustomModelDataComponent {

    public LegacyCustomModelDataComponent(List<ResolvableFloat> floats, List<ResolvableBoolean> booleans, List<ResolvableString> strings, List<ResolvableColor> colorList) {
        super(floats, booleans, strings, colorList);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        if (this.getFloats().isEmpty()) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        this.applyResolvable(context, value -> itemMeta.setCustomModelData((int) value.floatValue()), this.getFloats().getFirst());

        itemStack.setItemMeta(itemMeta);
    }
}
