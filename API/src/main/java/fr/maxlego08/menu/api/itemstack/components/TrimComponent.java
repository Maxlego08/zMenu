package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableArmorTrim;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class TrimComponent extends ItemComponent {
    private final @Nullable ResolvableArmorTrim resolvableArmorTrim;

    public TrimComponent(@Nullable ResolvableArmorTrim resolvableArmorTrim) {
        this.resolvableArmorTrim = resolvableArmorTrim;
    }

    public @Nullable ResolvableArmorTrim getResolvableArmorTrim() {
        return this.resolvableArmorTrim;
    }

    @Override
    public @Nullable Object serialize() {
        if (this.resolvableArmorTrim == null) return null;
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("material", this.resolvableArmorTrim.material().serialize());
        map.put("pattern", this.resolvableArmorTrim.pattern().serialize());
        return map;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.resolvableArmorTrim, armorTrim -> itemStack.setData(DataComponentTypes.TRIM, ItemArmorTrim.itemArmorTrim(armorTrim).build()));
    }
}
